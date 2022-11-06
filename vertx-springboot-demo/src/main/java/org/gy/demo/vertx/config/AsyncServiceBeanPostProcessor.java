package org.gy.demo.vertx.config;

import io.vertx.serviceproxy.ServiceProxyBuilder;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.vertx.annotation.AsyncReference;
import org.gy.demo.vertx.util.VertxUtil;
import org.gy.demo.vertx.verticle.VertxHandlerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/5 20:55
 */
@Configuration
@Slf4j
public class AsyncServiceBeanPostProcessor implements BeanPostProcessor, DisposableBean {

    private static final Map<Class<?>, Object> asyncServiceCache = new ConcurrentHashMap<>();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> objClz = bean.getClass();
        objClz = AopUtils.isAopProxy(objClz) ? AopUtils.getTargetClass(bean) : objClz;

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

    private static Object getServiceReference(AsyncReference annotation, Class<?> fieldTypeClazz) throws Exception {
        Object serviceObj = asyncServiceCache.get(fieldTypeClazz);
        if (Objects.isNull(serviceObj)) {
            synchronized (asyncServiceCache) {
                serviceObj = asyncServiceCache.get(fieldTypeClazz);
                if (Objects.isNull(serviceObj)) {
                    serviceObj = getServiceInstance(fieldTypeClazz);
                    asyncServiceCache.put(fieldTypeClazz, serviceObj);
                }
            }
        }
        return serviceObj;
    }

    private static Object getServiceInstance(Class<?> aClass) {
        String address = VertxHandlerFactory.buildAsyncServiceAddress(aClass);
        return new ServiceProxyBuilder(VertxUtil.getVertxInstance()).setAddress(address).build(aClass);
    }

    @Override
    public void destroy() throws Exception {
        asyncServiceCache.clear();
    }
}
