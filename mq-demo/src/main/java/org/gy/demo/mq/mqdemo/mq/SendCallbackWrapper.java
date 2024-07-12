package org.gy.demo.mq.mqdemo.mq;

import java.util.function.Consumer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.gy.demo.mq.mqdemo.trace.TraceContext;
import org.gy.demo.mq.mqdemo.trace.TraceEnum;
import org.slf4j.MDC;

/**
 * @author gy
 */
public class SendCallbackWrapper implements SendCallback {

    private final SendCallback sendCallback;

    private final String traceId;

    private final transient Thread callThread;

    private SendCallbackWrapper(SendCallback sendCallback) {
        this.sendCallback = sendCallback;
        this.traceId = MDC.get(TraceEnum.TRACE.getName());
        this.callThread = Thread.currentThread();
    }

    public static SendCallback of(SendCallback sendCallback) {
        return new SendCallbackWrapper(sendCallback);
    }

    @Override
    public void onSuccess(SendResult sendResult) {
        traceWrap(sendResult, sendCallback::onSuccess);
    }

    @Override
    public void onException(Throwable e) {
        traceWrap(e, sendCallback::onException);
    }

    private <T> void traceWrap(T t, Consumer<T> consumer) {
        // 如果回调在当前线程，则直接执行
        if (callThread == Thread.currentThread()) {
            consumer.accept(t);
            return;
        }
        TraceContext.setTrace(traceId);
        try {
            consumer.accept(t);
        } finally {
            TraceContext.clearTrace();
        }
    }
}
