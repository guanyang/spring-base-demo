package org.gy.demo.vertx.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 异步服务标识
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/4 14:44
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AsyncService {

    /**
     * beanName定义
     */
    String value() default "";

    /**
     * bean实现的接口定义
     */
    Class<?> interfaceClass();

    /**
     * 服务注册地址
     */
    String address() default "";

    /**
     * 超时时间，大于0生效，单位：毫秒
     */
    long timeout() default 0;

}
