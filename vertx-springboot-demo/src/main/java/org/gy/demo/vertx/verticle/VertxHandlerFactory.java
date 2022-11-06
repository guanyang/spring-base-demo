package org.gy.demo.vertx.verticle;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.serviceproxy.ServiceBinder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
        ServiceBinder binder = new ServiceBinder(vertx);
        asyncServiceClazzs.forEach(serviceClazz -> registerAsyncService(binder, serviceClazz));
    }

    private static <T> void registerAsyncService(ServiceBinder binder, Class<?> serviceClazz) {
        AsyncService asyncHandler = AnnotationUtils.findAnnotation(serviceClazz, AsyncService.class);
        Assert.notNull(asyncHandler, () -> "AsyncServiceHandler Annotation is required!");

        Class<T> interfaceClass = (Class<T>) asyncHandler.interfaceClass();
        Assert.notNull(interfaceClass, () -> "AsyncServiceHandler interfaceClass is required!");

        T asInstance = SpringContextUtil.getBean(interfaceClass);
        Assert.notNull(asInstance, () -> interfaceClass.getSimpleName() + " instance is null in ApplicationContext");

        String address = buildAsyncServiceAddress(interfaceClass);
        binder.setAddress(address).register(interfaceClass, asInstance);
    }

    public static <T> String buildAsyncServiceAddress(Class<T> interfaceClass) {
        return interfaceClass.getName();
    }

    private static void registerRouteHandler(Router router, Class<?> handler) {
        RouteMapping classRoute = AnnotationUtils.findAnnotation(handler, RouteMapping.class);
        Assert.notNull(classRoute, () -> "RouteMapping Annotation is required!");

        Object handlerInstance = SpringContextUtil.getBean(handler);
        Assert.notNull(handlerInstance, () -> handler.getSimpleName() + " instance is null in ApplicationContext");

        String[] rootPaths = classRoute.value();
        RouteMethod[] rootMethods = classRoute.method();
        List<Method> methods = getRouteMethod(handler);
        for (Method method : methods) {
            RouteMapping mapping = method.getAnnotation(RouteMapping.class);
            String[] paths = mapping.value();
            if (ObjectUtils.isEmpty(paths)) {
                continue;
            }
            RouteMethod[] routeMethods = mapping.method();
            if (ObjectUtils.isEmpty(routeMethods)) {
                routeMethods = rootMethods;
            }
            Handler<RoutingContext> methodHandler = (Handler<RoutingContext>) ReflectUtil.invoke(handlerInstance,
                method);
            if (methodHandler == null) {
                continue;
            }
            List<String> routeUrls = Stream.of(paths).map(p -> {
                if (ObjectUtils.isEmpty(rootPaths)) {
                    return Collections.singletonList(pathJoin(p));
                }
                return Stream.of(rootPaths).map(r -> pathJoin(r, p)).collect(Collectors.toList());
            }).flatMap(List::stream).collect(Collectors.toList());
            List<HttpMethod> httpMethods = Stream.of(routeMethods).map(RouteMethod::nameOf)
                .collect(Collectors.toList());
            registerRouteHandler(router, methodHandler, routeUrls, httpMethods);
        }
    }

    private static void registerRouteHandler(Router router, Handler<RoutingContext> methodHandler,
        List<String> routeUrls, List<HttpMethod> httpMethods) {
        routeUrls.forEach(path -> {
            httpMethods.forEach(m -> {
                router.route(m, path).handler(methodHandler);
            });
        });
    }

    private static List<Method> getRouteMethod(Class<?> handler) {
        Method[] methods = ReflectionUtils.getUniqueDeclaredMethods(handler);
        return Stream.of(methods).filter(m -> m.isAnnotationPresent(RouteMapping.class))
            .sorted(ROUTE_MAPPING_COMPARATOR).collect(Collectors.toList());
    }

    private static Set<Class<?>> scanAsyncService(String asyncServicePackageName) {
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
