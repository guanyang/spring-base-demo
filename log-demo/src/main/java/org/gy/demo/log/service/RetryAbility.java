package org.gy.demo.log.service;

import cn.hutool.extra.spring.SpringUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.gy.framework.core.exception.RetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author guanyang
 */
@Service
public class RetryAbility {

    @Retryable(value = RetryException.class, maxAttempts = 3, backoff = @Backoff(delay = 100, maxDelay = 300, multiplier = 2))
    public <T, R> R execute(T t, Function<T, R> function) {
        return function.apply(t);
    }

    @Retryable(value = RetryException.class, maxAttempts = 3, backoff = @Backoff(delay = 100, maxDelay = 300, multiplier = 2))
    public <T> void execute(T t, Consumer<T> consumer) {
        consumer.accept(t);
    }

    @Async
    @Retryable(value = RetryException.class, maxAttempts = 3, backoff = @Backoff(delay = 100, maxDelay = 300, multiplier = 2))
    public <T> void asyncExecute(T t, Consumer<T> consumer) {
        consumer.accept(t);
    }

    public final <T> void executeWithCallback(T t, Consumer<T> exeConsumer, Consumer<T> successCallback, Consumer<T>... errorCallbacks) {
        try {
            exeConsumer.accept(t);
            //异步处理，不影响业务
            asyncCallback(t, successCallback);
        } catch (Throwable e) {
            //异步处理，不影响业务
            asyncCallback(t, errorCallbacks);
            throw e;
        }
    }

    private static <T> void asyncCallback(T t, Consumer<T>... callbackConsumers) {
        if (ArrayUtils.isEmpty(callbackConsumers)) {
            return;
        }
        //基于代理类处理
        RetryAbility retryAbility = SpringUtil.getBean(RetryAbility.class);
        for (Consumer<T> callbackConsumer : callbackConsumers) {
            //异步处理，不影响业务
            Optional.ofNullable(retryAbility).ifPresent(r -> r.asyncExecute(t, callbackConsumer));
        }
    }
}
