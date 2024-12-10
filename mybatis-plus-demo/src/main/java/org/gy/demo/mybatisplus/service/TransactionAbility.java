package org.gy.demo.mybatisplus.service;

import lombok.Data;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author guanyang
 */
@Data
public class TransactionAbility {

    @Transactional(rollbackFor = Exception.class)
    public <T, R> R execute(T t, Function<T, R> function) {
        return function.apply(t);
    }

    @Transactional(rollbackFor = Exception.class)
    public <T> void execute(T t, Consumer<T> consumer) {
        consumer.accept(t);
    }

    @Async
    @Transactional(rollbackFor = Exception.class)
    public <T> void asyncExecute(T t, Consumer<T> consumer) {
        consumer.accept(t);
    }
}
