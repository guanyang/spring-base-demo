package org.gy.demo.mq.mqdemo.trace;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

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
