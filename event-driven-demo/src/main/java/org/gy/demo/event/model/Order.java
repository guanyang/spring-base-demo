package org.gy.demo.event.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Order implements Serializable {
    private static final long serialVersionUID = -4017587719069243706L;

    private String orderNum;

    private long orderTime = System.currentTimeMillis();
}
