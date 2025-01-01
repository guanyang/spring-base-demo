package org.gy.demo.mq.mqdemo.mq.support;

import brave.ScopedSpan;
import brave.Span;
import brave.Span.Kind;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import org.apache.rocketmq.client.hook.SendMessageContext;
import org.apache.rocketmq.client.hook.SendMessageHook;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.trace.TraceConstants;
import org.apache.rocketmq.common.message.Message;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @author guanyang
 */
public class SendMessageTracingHookImpl implements SendMessageHook {

    private static final String DEFAULT_HOOK_NAME = "SendMessageTracingHook";

    private final Tracer tracer;

    public SendMessageTracingHookImpl() {
        this.tracer = Tracing.currentTracer();
    }

    @Override
    public String hookName() {
        return DEFAULT_HOOK_NAME;
    }

    @Override
    public void sendMessageBefore(SendMessageContext context) {
        if (context == null) {
            return;
        }
        Message msg = context.getMessage();
        TraceContext traceContext = MessageTraceUtils.build(msg);
        ScopedSpan span = tracer.startScopedSpanWithParent(TraceConstants.FROM_PREFIX + msg.getTopic(), traceContext);
        Map<String, String> properties = Optional.ofNullable(msg.getProperties()).orElseGet(Collections::emptyMap);
        properties.forEach(span::tag);

        span.tag(TraceConstants.ROCKETMQ_TAGS, msg.getTags());
        span.tag(TraceConstants.ROCKETMQ_KEYS, msg.getKeys());
        span.tag(TraceConstants.ROCKETMQ_SOTRE_HOST, context.getBrokerAddr());
        span.tag(TraceConstants.ROCKETMQ_MSG_TYPE, context.getMsgType().name());
        span.tag(TraceConstants.ROCKETMQ_BODY_LENGTH, String.valueOf(msg.getBody().length));
        context.setMqTraceContext(span);
    }

    @Override
    public void sendMessageAfter(SendMessageContext context) {
        if (context == null || context.getMqTraceContext() == null) {
            return;
        }
        if (context.getSendResult() == null) {
            return;
        }

        if (context.getSendResult().getRegionId() == null) {
            return;
        }

        ScopedSpan span = (ScopedSpan) context.getMqTraceContext();
        span.tag(TraceConstants.ROCKETMQ_SUCCESS, String.valueOf(context.getSendResult().getSendStatus().equals(SendStatus.SEND_OK)));
        span.tag(TraceConstants.ROCKETMQ_MSG_ID, context.getSendResult().getMsgId());
        span.tag(TraceConstants.ROCKETMQ_REGION_ID, context.getSendResult().getRegionId());
        span.finish();
    }
}
