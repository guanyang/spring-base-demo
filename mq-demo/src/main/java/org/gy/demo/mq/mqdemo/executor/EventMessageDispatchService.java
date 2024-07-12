package org.gy.demo.mq.mqdemo.executor;


import org.gy.demo.mq.mqdemo.model.EventStringMessage;

/**
 * @author gy
 */
public interface EventMessageDispatchService {

    void execute(EventStringMessage eventMessage);
}
