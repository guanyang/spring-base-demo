package org.gy.demo.kafka.kafkademo.trace;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author gy
 */
@Data
@Accessors(chain = true)
public class Trace implements Serializable {

    private static final long serialVersionUID = -1306592113337318245L;

    private String traceId;
    private String spanId;


}
