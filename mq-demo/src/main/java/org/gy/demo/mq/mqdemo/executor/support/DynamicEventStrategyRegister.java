package org.gy.demo.mq.mqdemo.executor.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.gy.demo.mq.mqdemo.executor.EventMessageService;
import org.gy.demo.mq.mqdemo.model.DynamicEventContext;
import org.gy.demo.mq.mqdemo.model.EventType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodIntrospector.MetadataLookup;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author guanyang
 */
@Slf4j
public class DynamicEventStrategyRegister implements BeanDefinitionRegistryPostProcessor, SmartInitializingSingleton, Ordered {

    public static final String LINE_DELIMITER = "_";

    private BeanDefinitionRegistry beanDefinitionRegistry;

    private ConfigurableListableBeanFactory configurableListableBeanFactory;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        this.configurableListableBeanFactory = configurableListableBeanFactory;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }

    @Override
    public void afterSingletonsInstantiated() {
        String[] beanDefinitionNames = configurableListableBeanFactory.getBeanNamesForType(Object.class, false, true);
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = configurableListableBeanFactory.getBean(beanDefinitionName);
            Class<?> beanClass = bean.getClass();
            Map<Method, DynamicEventStrategy> methodMap = findMethod(beanDefinitionName, beanClass);
            if (MapUtils.isEmpty(methodMap)) {
                continue;
            }
            for (Map.Entry<Method, DynamicEventStrategy> entry : methodMap.entrySet()) {
                Method method = entry.getKey();
                DynamicEventStrategy annotation = entry.getValue();
                DynamicEventContext<Object, Object> ctx = build(bean, method, annotation);
                // 创建 Bean 定义
                BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DefaultDynamicEventMessageServiceImpl.class);
                beanDefinitionBuilder.addConstructorArgValue(ctx);
                // 注册 Bean
                String beanName = uniqueKey(beanClass.getSimpleName(), method.getName());
                beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());

                EventMessageService registerBean = (EventMessageService) configurableListableBeanFactory.getBean(beanName);
                EventMessageServiceManager.register(registerBean);
            }
        }
    }


    private static DynamicEventContext<Object, Object> build(Object bean, Method method, DynamicEventStrategy annotation) {
        Class<?> clazz = bean.getClass();
        String methodName = method.getName();
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != 1) {
            throw new IllegalStateException("DynamicEventStrategy method invalid, only one param support, for[" + clazz + "#" + methodName + "].");
        }
        method.setAccessible(true);
        Function<Object, Object> executeFunction = data -> ReflectionUtils.invokeMethod(method, bean, data);
        Predicate<Throwable> supportRetry = DynamicEventContext.getRetryPredicate(annotation.supportRetry());

        Class<?> dataType = paramTypes[0];
        EventType eventType = annotation.eventType();

        return new DynamicEventContext<>(eventType, dataType, executeFunction, supportRetry);
    }


    private static Map<Method, DynamicEventStrategy> findMethod(String beanDefinitionName, Class<?> clazz) {
        Map<Method, DynamicEventStrategy> annotatedMethods = null;
        try {
            annotatedMethods = MethodIntrospector.selectMethods(clazz, (MetadataLookup<DynamicEventStrategy>) method -> AnnotatedElementUtils.findMergedAnnotation(method, DynamicEventStrategy.class));
        } catch (Throwable ex) {
            log.warn("DynamicEventStrategyRegister findMethod error for bean[{}].", beanDefinitionName, ex);
        }
        return annotatedMethods;
    }

    public static String uniqueKey(Object... keys) {
        return StringUtils.joinWith(LINE_DELIMITER, keys);
    }
}
