package org.gy.demo.mq.mqdemo.mq.support;

import brave.Span;
import brave.Tracer;
import brave.Tracing;
import brave.propagation.TraceContext;
import org.apache.commons.collections.MapUtils;
import org.apache.rocketmq.common.message.Message;
import org.gy.demo.mq.mqdemo.trace.TraceEnum;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * @author guanyang
 */
public class MessageTraceUtils {

    public static void wrapTrace(Message msg) {
        TraceContext traceContext = Optional.ofNullable(Tracing.currentTracer()).map(Tracer::currentSpan).map(Span::context).orElse(null);
        if (traceContext != null) {
            Long traceId = traceContext.traceId();
            msg.putUserProperty(TraceEnum.TRACE.getName(), String.valueOf(traceId));

            Long spanId = traceContext.spanId();
            msg.putUserProperty(TraceEnum.SPAN.getName(), String.valueOf(spanId));

            Boolean sampled = traceContext.sampled();
            msg.putUserProperty(TraceEnum.SAMPLE.getName(), String.valueOf(sampled));
        }
    }

    public static TraceContext build(Message msg) {
        Map<String, String> properties = Optional.ofNullable(msg.getProperties()).orElseGet(Collections::emptyMap);
        long traceId = MapUtils.getLongValue(properties, TraceEnum.TRACE.getName());
        long spanId = MapUtils.getLongValue(properties, TraceEnum.SPAN.getName());
        boolean sampled = MapUtils.getBooleanValue(properties, TraceEnum.SPAN.getName());
        return TraceContext.newBuilder().traceId(traceId).spanId(spanId).sampled(sampled).build();
    }
}
