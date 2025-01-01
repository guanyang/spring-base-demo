package org.gy.demo.mq.mqdemo.mq.support;

import brave.ScopedSpan;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import org.apache.commons.collections.MapUtils;
import org.apache.rocketmq.client.hook.ConsumeMessageContext;
import org.apache.rocketmq.client.hook.ConsumeMessageHook;
import org.apache.rocketmq.client.trace.TraceConstants;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.gy.demo.mq.mqdemo.trace.TraceEnum;

import java.util.*;

/**
 * @author guanyang
 */
public class ConsumeMessageTracingHookImpl implements ConsumeMessageHook {

    private static final String DEFAULT_HOOK_NAME = "ConsumeMessageTracingHook";

    private final Tracer tracer;

    public ConsumeMessageTracingHookImpl() {
        this.tracer = Tracing.currentTracer();
    }

    @Override
    public String hookName() {
        return DEFAULT_HOOK_NAME;
    }

    @Override
    public void consumeMessageBefore(ConsumeMessageContext context) {
        if (context == null || context.getMsgList() == null || context.getMsgList().isEmpty()) {
            return;
        }
        List<ScopedSpan> spanList = new ArrayList<>();
        for (MessageExt msg : context.getMsgList()) {
            if (msg == null) {
                continue;
            }
            TraceContext traceContext = MessageTraceUtils.build(msg);
            ScopedSpan span = tracer.startScopedSpanWithParent(TraceConstants.FROM_PREFIX + msg.getTopic(), traceContext);
            Map<String, String> properties = Optional.ofNullable(msg.getProperties()).orElseGet(Collections::emptyMap);
            properties.forEach(span::tag);

            span.tag(TraceConstants.ROCKETMQ_MSG_ID, msg.getMsgId());
            span.tag(TraceConstants.ROCKETMQ_TAGS, msg.getTags());
            span.tag(TraceConstants.ROCKETMQ_KEYS, msg.getKeys());
            span.tag(TraceConstants.ROCKETMQ_BODY_LENGTH, String.valueOf(msg.getStoreSize()));
            span.tag(TraceConstants.ROCKETMQ_RETRY_TIMERS, String.valueOf(msg.getReconsumeTimes()));
            span.tag(TraceConstants.ROCKETMQ_REGION_ID, msg.getProperty(MessageConst.PROPERTY_MSG_REGION));
            spanList.add(span);
        }
        context.setMqTraceContext(spanList);
    }

    @Override
    public void consumeMessageAfter(ConsumeMessageContext context) {
        if (context == null || context.getMsgList() == null || context.getMsgList().isEmpty()) {
            return;
        }
        List<ScopedSpan> spanList = (List<ScopedSpan>) context.getMqTraceContext();
        if (spanList == null) {
            return;
        }
        for (ScopedSpan span : spanList) {
            span.tag(TraceConstants.ROCKETMQ_SUCCESS, String.valueOf(context.isSuccess()));
            span.finish();
        }
    }
}
