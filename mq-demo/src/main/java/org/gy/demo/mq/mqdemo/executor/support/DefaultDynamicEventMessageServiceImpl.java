package org.gy.demo.mq.mqdemo.executor.support;


import org.gy.demo.mq.mqdemo.executor.impl.AbstractEventMessageService;
import org.gy.demo.mq.mqdemo.model.DynamicEventContext;
import org.gy.demo.mq.mqdemo.model.EventType;

import java.util.Objects;

/**
 * @author guanyang
 */
public class DefaultDynamicEventMessageServiceImpl extends AbstractEventMessageService<Object, Object> {

    private final DynamicEventContext context;

    public DefaultDynamicEventMessageServiceImpl(DynamicEventContext context) {
        this.context = Objects.requireNonNull(context, "DynamicEventContext is required!");
    }

    @Override
    protected Class<Object> getDataType() {
        return context.getDataType();
    }

    @Override
    protected Object internalExecute(Object data) {
        return context.getExecuteFunction().apply(data);
    }

    @Override
    public EventType getEventType() {
        return context.getEventType();
    }

    @Override
    public boolean supportRetry(Throwable ex) {
        return context.getSupportRetry().test(ex);
    }
}
