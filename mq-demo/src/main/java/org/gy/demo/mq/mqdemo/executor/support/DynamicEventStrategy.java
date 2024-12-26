package org.gy.demo.mq.mqdemo.executor.support;


import org.gy.demo.mq.mqdemo.model.EventType;

import java.lang.annotation.*;

/**
 * 动态事件策略注解
 *
 * @author guanyang
 * @version 1.0.0
 */
@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DynamicEventStrategy {

    /**
     * 事件类型
     */
    EventType eventType();

    /**
     * 支持重试的异常
     */
    Class<? extends Throwable>[] supportRetry() default {Exception.class, Error.class};
}
