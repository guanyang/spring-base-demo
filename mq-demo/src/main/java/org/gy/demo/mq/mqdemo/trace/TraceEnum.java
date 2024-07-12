package org.gy.demo.mq.mqdemo.trace;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gy
 */

@Getter
@AllArgsConstructor
public enum TraceEnum {

    TRACE("TRACE", "x-trace-id"),

    SPAN("SPAN", "x-span-id");

    private final String id;
    private final String name;
}
