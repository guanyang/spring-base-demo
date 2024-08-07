package org.gy.demo.mq.mqdemo.config;

import org.gy.demo.mq.mqdemo.trace.TraceContext;
import org.springframework.core.task.TaskDecorator;


/**
 * @author gy
 */
public class ContextCopyingDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        String traceId = TraceContext.getTraceId();
//        Map<String, String> previous = MDC.getCopyOfContextMap();
        return () -> {
            try {
                TraceContext.setTrace(traceId);
//                MDC.setContextMap(previous);
                runnable.run();
            } finally {
                TraceContext.clearTrace();
//                MDC.clear();
            }
        };
    }
}
