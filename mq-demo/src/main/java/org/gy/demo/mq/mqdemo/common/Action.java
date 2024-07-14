package org.gy.demo.mq.mqdemo.common;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author gy
 */
@Slf4j
public class Action {

    @FunctionalInterface
    public interface BiFunctionAction<T, U, R> {

        R apply(T t, U u) throws Exception;

        @SneakyThrows
        static <T, U, R> R apply(T t, U u, BiFunctionAction<T, U, R> action) {
            return action.apply(t, u);
        }
    }

    @FunctionalInterface
    public interface ConsumerAction<T> {

        void accept(T t) throws Exception;

        @SneakyThrows
        static <T> void accept(T t, ConsumerAction<T> consumer) {
            consumer.accept(t);
        }

        static <T> void acceptQuietly(T t, ConsumerAction<T> action) {
            if (t == null) {
                return;
            }
            try {
                action.accept(t);
            } catch (Throwable ignore) {
                log.warn("ConsumerAction acceptQuietly error.", ignore);
            }
        }

    }

}
