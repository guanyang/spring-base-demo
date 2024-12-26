package org.gy.demo.mq.mqdemo.executor;


import com.google.common.collect.Lists;
import org.gy.demo.mq.mqdemo.model.EventMessage;
import org.gy.demo.mq.mqdemo.model.EventType;

import java.util.List;

/**
 * @author gy
 */
public interface EventMessageService<T, R> {

    EventType getEventType();

    R execute(EventMessage<T> eventMessage);

    default boolean supportRetry(Throwable ex) {
        return ex instanceof Exception || ex instanceof Error;
    }

    /**
     * 异步发送消息
     *
     * @param t 消息体
     */
    default void asyncSend(T t) {
        EventMessage<T> req = EventMessage.of(getEventType(), t);
        asyncSend(req);
    }

    /**
     * 异步发送消息（延时消息）
     *
     * @param t              消息体
     * @param delayTimeLevel 延时等级, 参考：EventSendReq#delayTimeLevel
     */
    default void asyncSend(T t, int delayTimeLevel) {
        EventMessage<T> req = EventMessage.of(getEventType(), t);
        req.setDelayTimeLevel(delayTimeLevel);
        asyncSend(req);
    }

    /**
     * 异步发送消息
     *
     * @param req 消息体req
     */
    default void asyncSend(EventMessage<T> req) {
        asyncSend(Lists.newArrayList(req));
    }

    /**
     * 批量异步发送消息（注意：批量消息不支持延迟、顺序消费）
     *
     * @param reqs 批量消息体reqs
     */
    void asyncSend(List<EventMessage<T>> reqs);

    /**
     * 异步发送消息（特殊场景，仅异步处理）
     *
     * @param req 消息体req
     */
    default void asyncSendInternal(EventMessage<T> req) {
        asyncSend(req);
    }

    /**
     * 同步发送消息（特殊场景，仅同步处理）
     *
     * @param req 消息体req
     * @return 返回结果
     */
    default R synchronousSend(EventMessage<T> req) {
        throw new UnsupportedOperationException();
    }
}
