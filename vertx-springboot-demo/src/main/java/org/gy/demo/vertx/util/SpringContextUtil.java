package org.gy.demo.vertx.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring上下文工具
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 实现ApplicationContextAware接口的回调方法
     */
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 获取对象
     *
     * @return Object 一个以所给名字注册的bean的实例(必须遵循Spring的生成规则)
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }


    public static <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        return applicationContext.getBean(name, requiredType);
    }

    /**
     * 获取对象
     *
     * @return Object 一个以所给名字注册的bean的实例(必须遵循Spring的生成规则)
     */
    public static <T> T getBean(Class<T> requiredType) throws BeansException {
        return applicationContext.getBean(requiredType);
    }
}