package org.gy.demo.mq.mqdemo.mq.support;

import brave.ScopedSpan;
import brave.Span;
import brave.Span.Kind;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import io.opentracing.tag.Tags;
import org.apache.rocketmq.client.hook.EndTransactionContext;
import org.apache.rocketmq.client.hook.EndTransactionHook;
import org.apache.rocketmq.client.trace.TraceConstants;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageType;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @author guanyang
 */
public class EndTransactionTracingHookImpl implements EndTransactionHook {

    private static final String DEFAULT_HOOK_NAME = "EndTransactionTracingHook";

    private final Tracer tracer;

    public EndTransactionTracingHookImpl() {
        this.tracer = Tracing.currentTracer();
    }

    @Override
    public String hookName() {
        return DEFAULT_HOOK_NAME;
    }

    @Override
    public void endTransaction(EndTransactionContext context) {
        if (context == null) {
            return;
        }
        Message msg = context.getMessage();
        TraceContext traceContext = MessageTraceUtils.build(msg);
        ScopedSpan span = tracer.startScopedSpanWithParent(TraceConstants.END_TRANSACTION, traceContext);
        Map<String, String> properties = Optional.ofNullable(msg.getProperties()).orElseGet(Collections::emptyMap);
        properties.forEach(span::tag);

        span.tag(TraceConstants.ROCKETMQ_TAGS, msg.getTags());
        span.tag(TraceConstants.ROCKETMQ_KEYS, msg.getKeys());
        span.tag(TraceConstants.ROCKETMQ_SOTRE_HOST, context.getBrokerAddr());
        span.tag(TraceConstants.ROCKETMQ_MSG_ID, context.getMsgId());
        span.tag(TraceConstants.ROCKETMQ_MSG_TYPE, MessageType.Trans_msg_Commit.name());
        span.tag(TraceConstants.ROCKETMQ_TRANSACTION_ID, context.getTransactionId());
        span.tag(TraceConstants.ROCKETMQ_TRANSACTION_STATE, context.getTransactionState().name());
        span.tag(TraceConstants.ROCKETMQ_IS_FROM_TRANSACTION_CHECK, String.valueOf(context.isFromTransactionCheck()));
        span.finish();
    }
}
