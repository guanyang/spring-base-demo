package org.gy.demo.mq.mqdemo.executor;


import org.gy.demo.mq.mqdemo.model.EventMessage;
import org.gy.demo.mq.mqdemo.model.EventMessageDispatchResult;

/**
 * @author gy
 */
public interface EventMessageDispatchService {

    EventMessageDispatchResult execute(EventMessage<?> eventMessage);
}
