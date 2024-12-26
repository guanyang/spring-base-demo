package org.gy.demo.mq.mqdemo.model;

import cn.hutool.core.lang.Assert;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gy.framework.core.support.IStdEnum;

/**
 * 功能描述：消息事件类型
 *
 * @author gy
 * @version 1.0.0
 */
@Getter
@AllArgsConstructor
public enum EventType implements IStdEnum<Integer> {

    //消息事件类型
    DEMO_EVENT(0, "示例事件"),

    DYNAMIC_DEMO_EVENT(1, "动态示例事件");

    private final Integer code;

    private final String desc;

    public static EventType codeOf(Integer code) {
        EventType deletedEnum = EventType.codeOf(code, null);
        Assert.notNull(deletedEnum, "unknown EventType code:" + code);
        return deletedEnum;
    }

    public static EventType codeOf(Integer code, EventType defaultEnum) {
        return IStdEnum.codeOf(EventType.class, code, defaultEnum);
    }

}
