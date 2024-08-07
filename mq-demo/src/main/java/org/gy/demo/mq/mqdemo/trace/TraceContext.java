package org.gy.demo.mq.mqdemo.trace;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import org.slf4j.MDC;

/**
 * @author gy
 */
public class TraceContext {


    public static void setTrace(String traceId) {
        if (StrUtil.isBlank(traceId)) {
            traceId = randomTraceId();
        }
        MDC.put(TraceEnum.TRACE.getName(), traceId);
        MDC.put(TraceEnum.SPAN.getName(), randomSpanId());
    }


    public static String getTraceId() {
        return getTrace().getTraceId();
    }

    public static Trace getTrace() {
        Trace trace = new Trace();
        String traceId = MDC.get(TraceEnum.TRACE.getName());
        if (StrUtil.isBlank(traceId)) {
            trace.setTraceId(randomTraceId());
            MDC.put(TraceEnum.TRACE.getName(), trace.getTraceId());
        } else {
            trace.setTraceId(traceId);
        }
        String spanId = MDC.get(TraceEnum.SPAN.getName());
        if (StrUtil.isBlank(spanId)) {
            trace.setSpanId(randomSpanId());
            MDC.put(TraceEnum.SPAN.getName(), trace.getSpanId());
        } else {
            trace.setSpanId(spanId);
        }
        return trace;
    }

    public static void clearTrace() {
        MDC.remove(TraceEnum.TRACE.getName());
        MDC.remove(TraceEnum.SPAN.getName());
    }

    private static String randomSpanId() {
        return RandomUtil.randomString(16);
    }

    private static String randomTraceId() {
        return RandomUtil.randomString(16);
    }

}
