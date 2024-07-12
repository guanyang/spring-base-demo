package org.gy.demo.mq.mqdemo.executor.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.TypeReference;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.mq.mqdemo.model.EventStringMessage;
import org.gy.demo.mq.mqdemo.model.EventType;
import org.springframework.stereotype.Service;

/**
 * 功能描述：示例事件处理
 *
 * @author gy
 */
@Slf4j
@Service
public class DemoEventMessageServiceImpl extends AbstractEventMessageService<String> {

    public static final TypeReference<String> DEFAULT_STRING_TYPE = new TypeReference<String>() {
    };


    @Override
    public EventType getEventType() {
        return EventType.DEMO_EVENT;
    }

    @Override
    public TypeReference<String> getDataType() {
        return DEFAULT_STRING_TYPE;
    }


    @Override
    protected String convert(EventStringMessage eventMessage) {
        return Optional.ofNullable(eventMessage).map(EventStringMessage::getData).orElse(StrUtil.EMPTY);
    }


    @Override
    protected void internalExecute(EventStringMessage sourceMessage, String data) {
        log.info("[DEMO_EVENT]消息数据:{}", data);
    }
}
