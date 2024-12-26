package org.gy.demo.mq.mqdemo.model;

import lombok.Data;
import org.gy.demo.mq.mqdemo.executor.EventMessageService;

import java.io.Serializable;

/**
 * @author guanyang
 */
@Data
public class EventMessageDispatchResult implements Serializable {
    private static final long serialVersionUID = 7377345643668821360L;

    private static final EventMessageDispatchResult NONE = new EventMessageDispatchResult();

    private Object result;

    private EventMessageService service;

    private Throwable ex;

    public boolean hasException() {
        return ex != null;
    }

    public static EventMessageDispatchResult of(Throwable ex) {
        EventMessageDispatchResult context = new EventMessageDispatchResult();
        context.ex = ex;
        return context;
    }

    public static EventMessageDispatchResult of(Object result, EventMessageService service) {
        EventMessageDispatchResult context = new EventMessageDispatchResult();
        context.result = result;
        context.service = service;
        return context;
    }

    public static EventMessageDispatchResult of(Throwable ex, EventMessageService service) {
        EventMessageDispatchResult context = new EventMessageDispatchResult();
        context.ex = ex;
        context.service = service;
        return context;
    }
}
