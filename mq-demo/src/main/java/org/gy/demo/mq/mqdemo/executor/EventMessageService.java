package org.gy.demo.mq.mqdemo.executor;


import org.gy.demo.mq.mqdemo.model.EventStringMessage;
import org.gy.demo.mq.mqdemo.model.EventType;

/**
 * @author gy
 */
public interface EventMessageService {

    EventType getEventType();

    void execute(EventStringMessage eventMessage);

    default boolean supportRetry(Throwable ex) {
        return ex instanceof Exception || ex instanceof Error;
    }
}
