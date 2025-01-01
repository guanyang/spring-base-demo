package org.gy.demo.mq.mqdemo.mq;

import brave.ScopedSpan;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;

import java.util.function.Consumer;

/**
 * @author gy
 */
public class SendCallbackWrapper implements SendCallback {

    private final static String SPAN_NAME = "SendCallbackWrapper";

    private final SendCallback sendCallback;

//    private final String traceId;

    private final TraceContext traceContext;

    private final Tracer tracer;

    private final transient Thread callThread;

    private SendCallbackWrapper(SendCallback sendCallback) {
        this.sendCallback = sendCallback;
//        this.traceId = MDC.get(TraceEnum.TRACE.getName());
        this.traceContext = Tracing.currentTracer().currentSpan().context();
        this.tracer = Tracing.currentTracer();
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

        ScopedSpan span = tracer.startScopedSpanWithParent(SPAN_NAME, traceContext);
        try {
            consumer.accept(t);
        } catch (Throwable e) {
            span.error(e);
            throw e;
        } finally {
            span.finish();
        }

//        TraceContext.setTrace(traceId);
//        try {
//            consumer.accept(t);
//        } finally {
//            TraceContext.clearTrace();
//        }
    }
}
