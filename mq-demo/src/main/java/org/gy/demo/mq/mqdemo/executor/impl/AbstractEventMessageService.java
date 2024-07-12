package org.gy.demo.mq.mqdemo.executor.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.mq.mqdemo.executor.EventMessageService;
import org.gy.demo.mq.mqdemo.model.EventStringMessage;
import org.gy.framework.core.exception.BizException;

/**
 * @author gy
 */
@Slf4j
public abstract class AbstractEventMessageService<T> implements EventMessageService {

    protected abstract void internalExecute(EventStringMessage sourceMessage, T data);

    protected abstract TypeReference<T> getDataType();

    protected T convert(EventStringMessage eventMessage) {
        try {
            return JSON.parseObject(eventMessage.getData(), getDataType());
        } catch (Exception e) {
            log.error("[EventMessageService]消息数据转换异常, event={}.", eventMessage, e);
            return null;
        }
    }

    @Override
    public void execute(EventStringMessage eventMessage) {
        Optional.ofNullable(convert(eventMessage)).ifPresent(data -> internalExecute(eventMessage, data));
    }

    @Override
    public boolean supportRetry(Throwable ex) {
        return !(ex instanceof BizException);
    }
}
