package org.gy.demo.vertx.verticle;

import static io.vertx.serviceproxy.ServiceBinder.DEFAULT_CONNECTION_TIMEOUT;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.serviceproxy.ServiceBinder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.vertx.annotation.AsyncService;
import org.gy.demo.vertx.annotation.RouteMapping;
import org.gy.demo.vertx.annotation.support.RouteMethod;
import org.gy.demo.vertx.util.SpringContextUtil;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/4 17:50
 */
@Slf4j
public class VertxHandlerFactory {

    public static final String PATH_SEPARATOR = "/";

    public static final String EMPTY = "";

    private static final Comparator<Class<?>> ROUTE_HANDLER_COMPARATOR = (c1, c2) -> {
        RouteMapping routeHandler1 = c1.getAnnotation(RouteMapping.class);
        RouteMapping routeHandler2 = c2.getAnnotation(RouteMapping.class);
        return Integer.compare(routeHandler2.order(), routeHandler1.order());
    };

    private static final Comparator<Method> ROUTE_MAPPING_COMPARATOR = (m1, m2) -> {
        RouteMapping mapping1 = m1.getAnnotation(RouteMapping.class);
        RouteMapping mapping2 = m2.getAnnotation(RouteMapping.class);
        return Integer.compare(mapping2.order(), mapping1.order());
    };

    public static void registerRouter(Router router, String routePackageName) {
        Set<Class<?>> routeHandlerClazzs = scanRouteHandler(routePackageName);
        routeHandlerClazzs.stream().sorted(ROUTE_HANDLER_COMPARATOR).forEach(handler -> {
            registerRouteHandler(router, handler);
        });
    }

    public static void registerAsyncService(Vertx vertx, String asyncServicePackageName) {
        Set<Class<?>> asyncServiceClazzs = scanAsyncService(asyncServicePackageName);
        asyncServiceClazzs.forEach(serviceClazz -> registerAsyncService(vertx, serviceClazz));
    }

    private static <T> void registerAsyncService(Vertx vertx, Class<?> serviceClazz) {
        AsyncService asyncHandler = AnnotationUtils.findAnnotation(serviceClazz, AsyncService.class);
        Assert.notNull(asyncHandler, () -> "AsyncServiceHandler Annotation is required!");

        Class<T> interfaceClass = (Class<T>) asyncHandler.interfaceClass();
        Assert.notNull(interfaceClass, () -> "AsyncServiceHandler interfaceClass is required!");

        T asInstance = SpringContextUtil.getBean(interfaceClass);
        Assert.notNull(asInstance, () -> interfaceClass.getSimpleName() + " instance is null in ApplicationContext");

        String address = Optional.ofNullable(asyncHandler.address()).filter(StrUtil::isNotBlank)
            .orElseGet(() -> buildAsyncServiceAddress(interfaceClass));
        long timeout = Optional.ofNullable(asyncHandler.timeout()).filter(i -> i > 0)
            .map(TimeUnit.MILLISECONDS::toSeconds).orElse(DEFAULT_CONNECTION_TIMEOUT);
        ServiceBinder binder = new ServiceBinder(vertx).setTimeoutSeconds(timeout);
        binder.setAddress(address).register(interfaceClass, asInstance);
    }

    public static <T> String buildAsyncServiceAddress(Class<T> interfaceClass) {
        return interfaceClass.getName();
    }

    private static void registerRouteHandler(Router router, Class<?> handler) {
        RouteMapping classMapping = AnnotationUtils.findAnnotation(handler, RouteMapping.class);
        Assert.notNull(classMapping, () -> "RouteMapping Annotation is required!");

        Object handlerInstance = SpringContextUtil.getBean(handler);
        Assert.notNull(handlerInstance, () -> handler.getSimpleName() + " instance is null in ApplicationContext");

        List<Method> methods = getRouteMethod(handler);
        for (Method method : methods) {
            RouteMapping methodMapping = method.getAnnotation(RouteMapping.class);
            if (methodMapping.exception()) {
                registerErrorRouteHandler(router, methodMapping, method, handlerInstance);
            } else {
                registerMethodRouteHandler(router, classMapping, methodMapping, method, handlerInstance);
            }
        }
    }

    /**
     * 注册异常处理路由
     */
    private static void registerErrorRouteHandler(Router router, RouteMapping methodMapping, Method method,
        Object handlerInstance) {
        int[] statusCodes = methodMapping.statusCode();
        if (ObjectUtils.isEmpty(statusCodes)) {
            return;
        }
        Handler<RoutingContext> methodHandler = (Handler<RoutingContext>) ReflectUtil.invoke(handlerInstance, method);
        if (methodHandler == null) {
            return;
        }
        Arrays.stream(statusCodes).forEach(code -> {
            router.errorHandler(code, methodHandler);
        });
    }

    /**
     * 注册方法请求路由
     */
    private static void registerMethodRouteHandler(Router router, RouteMapping classMapping, RouteMapping methodMapping,
        Method method, Object handlerInstance) {
        Handler<RoutingContext> methodHandler = (Handler<RoutingContext>) ReflectUtil.invoke(handlerInstance, method);
        if (methodHandler == null) {
            return;
        }
        RouteMethod[] routeMethods = methodMapping.method();
        if (ObjectUtils.isEmpty(routeMethods)) {
            routeMethods = classMapping.method();
        }
        List<HttpMethod> httpMethods = Stream.of(routeMethods).map(RouteMethod::nameOf).collect(Collectors.toList());
        String[] paths = methodMapping.value();
        String[] rootPaths = classMapping.value();
        List<String> routeUrls = Stream.of(paths).map(p -> {
            if (ObjectUtils.isEmpty(rootPaths)) {
                return Collections.singletonList(pathJoin(p));
            }
            return Stream.of(rootPaths).map(r -> pathJoin(r, p)).collect(Collectors.toList());
        }).flatMap(List::stream).collect(Collectors.toList());
        //是否开启正则路由
        boolean regex = methodMapping.regex();
        registerRouteHandler(router, methodHandler, routeUrls, httpMethods, regex);
    }

    private static void registerRouteHandler(Router router, Handler<RoutingContext> methodHandler,
        List<String> routeUrls, List<HttpMethod> httpMethods, boolean regex) {
        if (ObjectUtils.isEmpty(routeUrls)) {
            //注册全局handler
            router.route().handler(methodHandler);
            return;
        }
        //注册指定路由handler
        routeUrls.forEach(path -> {
            if (ObjectUtils.isEmpty(httpMethods)) {
                routeWrapper(router, path, null, regex).handler(methodHandler);
            } else {
                httpMethods.forEach(m -> {
                    routeWrapper(router, path, m, regex).handler(methodHandler);
                });
            }
        });
    }

    private static Route routeWrapper(Router router, String path, HttpMethod method, boolean regex) {
        Supplier<Route> routerSupplier;
        if (method == null) {
            routerSupplier = regex ? () -> router.routeWithRegex(path) : () -> router.route(path);
        } else {
            routerSupplier = regex ? () -> router.routeWithRegex(method, path) : () -> router.route(method, path);
        }
        return routerSupplier.get();
    }

    private static List<Method> getRouteMethod(Class<?> handler) {
        Method[] methods = ReflectionUtils.getUniqueDeclaredMethods(handler);
        return Stream.of(methods).filter(m -> m.isAnnotationPresent(RouteMapping.class))
            .sorted(ROUTE_MAPPING_COMPARATOR).collect(Collectors.toList());
    }

    public static Set<Class<?>> scanAsyncService(String asyncServicePackageName) {
        return scanPackageByAnnotation(asyncServicePackageName, AsyncService.class);
    }

    private static Set<Class<?>> scanRouteHandler(String routePackageName) {
        return scanPackageByAnnotation(routePackageName, RouteMapping.class);
    }

    private static Set<Class<?>> scanPackageByAnnotation(String packageName,
        final Class<? extends Annotation> annotationClass) {
        Objects.requireNonNull(packageName, "packageName is required!");
        Objects.requireNonNull(annotationClass, "annotationClass is required!");
        return ClassUtil.scanPackageByAnnotation(packageName, annotationClass);
    }

    private static String pathJoin(final String... path) {
        StringBuilder result = new StringBuilder(PATH_SEPARATOR);
        for (String p : path) {
            if (!result.toString().endsWith(PATH_SEPARATOR)) {
                result.append(PATH_SEPARATOR);
            }
            result.append(p.startsWith(PATH_SEPARATOR) ? p.replaceFirst(PATH_SEPARATOR, EMPTY) : p);
        }
        return result.toString();
    }
}
