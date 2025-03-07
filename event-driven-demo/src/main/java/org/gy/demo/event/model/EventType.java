package org.gy.demo.event.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gy.framework.core.support.IStdEnum;

import java.util.Objects;

/**
 * 功能描述：消息事件类型
 *
 * @author gy
 * @version 1.0.0
 */
@Getter
@AllArgsConstructor
public enum EventType implements IStdEnum<Integer>, IEventType {

    //消息事件类型
    DEMO_EVENT(0, "示例事件"),

    DYNAMIC_DEMO_EVENT(1, "动态示例事件");

    private final Integer code;

    private final String desc;

    public static EventType codeOf(Integer code) {
        EventType item = EventType.codeOf(code, null);
        return Objects.requireNonNull(item, () -> "unknown EventType error:" + code);
    }

    public static EventType codeOf(Integer code, EventType defaultEnum) {
        return IStdEnum.codeOf(EventType.class, code, defaultEnum);
    }

}
