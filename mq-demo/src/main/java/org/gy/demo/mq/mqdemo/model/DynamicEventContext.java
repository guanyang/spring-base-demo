package org.gy.demo.mq.mqdemo.model;

import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author guanyang
 */
@Data
public class DynamicEventContext<T, R> implements Serializable {
    private static final long serialVersionUID = -7227620910929562435L;

    public static final Predicate<Throwable> DEFAULT_PREDICATE = ex -> ex instanceof Exception || ex instanceof Error;

    private EventType eventType;

    private Class<?> dataType;

    private Function<T, R> executeFunction;

    private Predicate<Throwable> supportRetry;

    public DynamicEventContext(EventType eventType, Class<?> dataType, Function<T, R> executeFunction) {
        this(eventType, dataType, executeFunction, DEFAULT_PREDICATE);
    }

    public DynamicEventContext(EventType eventType, Class<?> dataType, Function<T, R> executeFunction, Predicate<Throwable> supportRetry) {
        this.eventType = Objects.requireNonNull(eventType, "eventType is required!");
        this.dataType = Objects.requireNonNull(dataType, "executeFunction is required!");
        this.executeFunction = Objects.requireNonNull(executeFunction, "executeFunction is required!");
        this.supportRetry = Objects.requireNonNull(supportRetry, "supportRetry is required!");
    }

    public static Predicate<Throwable> getRetryPredicate(Class<? extends Throwable>[] retryTypes) {
        if (ArrayUtils.isEmpty(retryTypes)) {
            return DynamicEventContext.DEFAULT_PREDICATE;
        }
        return Arrays.stream(retryTypes).map(type -> (Predicate<Throwable>) type::isInstance).reduce(Predicate::or).orElse(t -> false);
    }
}
