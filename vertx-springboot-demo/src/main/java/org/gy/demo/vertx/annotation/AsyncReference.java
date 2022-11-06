package org.gy.demo.vertx.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 异步服务消费注入
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/5 00:43
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AsyncReference {

    /**
     * 服务注册地址
     */
    String address() default "";

    /**
     * 超时时间，单位：毫秒
     */
    long timeout() default 0;

}
