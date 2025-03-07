package org.gy.demo.event.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Log implements Serializable {
    private static final long serialVersionUID = 6848566245530631095L;

    private String logName;

    private long logTime = System.currentTimeMillis();
}
