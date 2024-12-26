package org.gy.demo.mq.mqdemo.executor.support;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.gy.demo.mq.mqdemo.model.EventLogContext;
import org.gy.demo.mq.mqdemo.model.EventMessage;
import org.gy.demo.mq.mqdemo.model.EventType;

import java.lang.reflect.Method;

/**
 * @author guanyang
 */
@Slf4j
@Aspect
public class DynamicEventStrategyAspect {

    @Pointcut("@annotation(org.gy.demo.mq.mqdemo.executor.support.DynamicEventStrategy)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        boolean currentServiceActive = EventMessageServiceManager.isCurrentServiceActive();
        //如果事件激活(本身已经记录日志)，则直接执行
        if (currentServiceActive) {
            return proceed(joinPoint);
        }
        //获取方法
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String methodName = method.getName();
        // 获取AspectAnnotation注解
        DynamicEventStrategy eventStrategy = method.getAnnotation(DynamicEventStrategy.class);
        EventType eventType = eventStrategy.eventType();
        log.info("[DynamicEventStrategyAspect][{}]接口日志处理：eventType={}", methodName, eventType);

        EventMessage<Object> req = buildEventSendReq(eventStrategy, joinPoint);
        return EventLogContext.handleWithLog(req, data -> proceed(joinPoint));
    }

    @SneakyThrows
    protected Object proceed(ProceedingJoinPoint joinPoint) {
        return joinPoint.proceed(joinPoint.getArgs());
    }

    protected EventMessage<Object> buildEventSendReq(DynamicEventStrategy eventStrategy, ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        Object data = null;
        if (ArrayUtils.isNotEmpty(args)) {
            data = args[0];
        }
        return EventMessage.of(eventStrategy.eventType(), data);
    }
}
