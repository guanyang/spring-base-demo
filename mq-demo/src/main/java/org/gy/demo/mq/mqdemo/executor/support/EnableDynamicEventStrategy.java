package org.gy.demo.mq.mqdemo.executor.support;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author guanyang
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({DynamicEventStrategyRegister.class, DynamicEventStrategyAspect.class})
public @interface EnableDynamicEventStrategy {
}
