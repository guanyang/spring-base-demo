package org.gy.demo.vertx.config;

import static org.gy.demo.vertx.verticle.VertxHandlerFactory.buildAsyncServiceAddress;
import static org.gy.demo.vertx.verticle.VertxHandlerFactory.scanAsyncService;
import static org.gy.demo.vertx.verticle.support.VerticleConstants.DEFAULT_PACKAGE;

import cn.hutool.core.util.StrUtil;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.serviceproxy.ServiceProxyBuilder;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.vertx.annotation.AsyncReference;
import org.gy.demo.vertx.annotation.AsyncService;
import org.gy.demo.vertx.util.VertxUtil;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/6 22:12
 */
@Slf4j
@Configuration
public class AsyncServiceBeanProcessor implements BeanDefinitionRegistryPostProcessor, BeanPostProcessor,
    DisposableBean {

    private static final Map<AsyncReferenceKey, Object> asyncServiceReferenceCache = new ConcurrentHashMap<>();

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        //扫描所有@AsyncService注解的类
        Set<Class<?>> asyncServiceClazzs = scanAsyncService(DEFAULT_PACKAGE);
        //注册异步服务到容器
        asyncServiceClazzs.forEach(clazz -> registerAsyncServiceBean(clazz, registry));
    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> objClz = bean.getClass();
        objClz = AopUtils.isAopProxy(objClz) ? AopUtils.getTargetClass(bean) : objClz;
        //扫描所有@AsyncReference注解的属性，注入代理对象
        Stream.of(objClz.getDeclaredFields()).forEach(field -> {
            AsyncReference annotation = field.getAnnotation(AsyncReference.class);
            if (Objects.isNull(annotation)) {
                return;
            }
            try {
                Class<?> fieldTypeClazz = field.getType();
                Object serviceObj = getServiceReference(annotation, fieldTypeClazz);
                field.setAccessible(true);
                field.set(bean, serviceObj);
                field.setAccessible(false);
            } catch (Exception e) {
                log.error("BeanPostProcessor exception, annotation:{}.", annotation, e);
                throw new BeanCreationException(beanName, e);
            }
        });

        return bean;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //do nothing
    }

    @Override
    public void destroy() throws Exception {
        asyncServiceReferenceCache.clear();
    }

    private static Object getServiceReference(AsyncReference annotation, Class<?> fieldTypeClazz) throws Exception {
        AsyncReferenceKey referenceKey = AsyncReferenceKey.of(annotation, fieldTypeClazz);
        Object serviceObj = asyncServiceReferenceCache.get(referenceKey);
        if (Objects.isNull(serviceObj)) {
            synchronized (asyncServiceReferenceCache) {
                serviceObj = asyncServiceReferenceCache.get(referenceKey);
                if (Objects.isNull(serviceObj)) {
                    serviceObj = getServiceInstance(referenceKey);
                    asyncServiceReferenceCache.put(referenceKey, serviceObj);
                }
            }
        }
        return serviceObj;
    }

    private static Object getServiceInstance(AsyncReferenceKey key) {
        DeliveryOptions options = new DeliveryOptions();
        options.setSendTimeout(key.timeout);
        return new ServiceProxyBuilder(VertxUtil.getVertxInstance()).setAddress(key.address).setOptions(options)
            .build(key.clazz);
    }

    /**
     * 注册异步服务到容器
     */
    private void registerAsyncServiceBean(Class<?> serviceClazz, BeanDefinitionRegistry registry) {
        AsyncService asyncHandler = AnnotationUtils.findAnnotation(serviceClazz, AsyncService.class);
        Assert.notNull(asyncHandler, () -> "AsyncServiceHandler Annotation is required!");

        String beanName = Optional.ofNullable(asyncHandler.value()).filter(StrUtil::isNotBlank)
            .orElseGet(serviceClazz::getSimpleName);
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(serviceClazz)
            .getBeanDefinition();
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    @Data
    @Builder
    public static class AsyncReferenceKey {

        private Class<?> clazz;

        private String address;

        private long timeout;

        public static AsyncReferenceKey of(AsyncReference annotation, Class<?> clazz) {
            String address = Optional.ofNullable(annotation.address()).filter(StrUtil::isNotBlank)
                .orElseGet(() -> buildAsyncServiceAddress(clazz));
            long timeout = Optional.ofNullable(annotation.timeout()).filter(i -> i > 0)
                .orElse(DeliveryOptions.DEFAULT_TIMEOUT);
            return AsyncReferenceKey.builder().clazz(clazz).address(address).timeout(timeout).build();
        }
    }
}
