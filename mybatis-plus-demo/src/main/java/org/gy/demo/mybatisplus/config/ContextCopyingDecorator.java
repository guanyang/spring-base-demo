package org.gy.demo.mybatisplus.config;

import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;


/**
 * @author gy
 */
public class ContextCopyingDecorator implements TaskDecorator {
    @Override
    public Runnable decorate(Runnable runnable) {
        Map<String, String> previous = MDC.getCopyOfContextMap();
        return () -> {
            try {
                MDC.setContextMap(previous);
                runnable.run();
            } finally {
                MDC.clear();
            }
        };
    }
}
