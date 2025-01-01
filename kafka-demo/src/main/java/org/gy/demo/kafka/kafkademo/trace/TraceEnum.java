package org.gy.demo.kafka.kafkademo.trace;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gy
 */

@Getter
@AllArgsConstructor
public enum TraceEnum {

    TRACE("TRACE", "x-trace-id", "traceId"),

    SPAN("SPAN", "x-span-id", "spanId"),

    SAMPLE("SAMPLE", "x-sample", "sample");

    private final String id;
    private final String name;
    private final String traceName;
}
