package org.gy.demo.mybatisplus.config;

import org.gy.demo.mybatisplus.trace.TraceContext;
import org.gy.demo.mybatisplus.trace.TraceEnum;
import org.gy.framework.mq.core.TraceService;

public class CustomTraceService implements TraceService {
    @Override
    public String getTraceKey() {
        return TraceEnum.TRACE.getName();
    }

    @Override
    public String getTraceId() {
        return TraceContext.getTraceId();
    }

    @Override
    public void setTrace(String traceId) {
        TraceContext.setTrace(traceId);
    }

    @Override
    public void clearTrace() {
        TraceContext.clearTrace();
    }
}
