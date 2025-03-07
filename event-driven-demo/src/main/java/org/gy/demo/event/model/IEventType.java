package org.gy.demo.event.model;

public interface IEventType {

    /**
     * 返回枚举实际值
     */
    Integer getCode();

    /**
     * 返回枚举描述说明
     */
    String getDesc();
}
