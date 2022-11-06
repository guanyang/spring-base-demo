package org.gy.demo.vertx.annotation;

import io.vertx.core.http.HttpMethod;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.gy.demo.vertx.annotation.support.RouteMethod;

/**
 * Router API Mehtod 标识注解
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/4 14:25
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RouteMapping {

    /**
     * 请求路径
     */
    String[] value() default {};

    /**
     * 请求方法，对应HttpMethod
     *
     * @see HttpMethod
     */
    RouteMethod[] method() default {};

    /**
     * 注册顺序，数字越大优先级越高
     */
    int order() default 0;

    /**
     * 接口描述
     */
    String desc() default "";


}
