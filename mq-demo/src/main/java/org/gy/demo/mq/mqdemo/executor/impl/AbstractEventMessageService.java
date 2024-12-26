package org.gy.demo.mq.mqdemo.executor.impl;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gy.demo.mq.mqdemo.executor.EventMessageSendService;
import org.gy.demo.mq.mqdemo.executor.EventMessageService;
import org.gy.demo.mq.mqdemo.executor.support.EventMessageServiceManager;
import org.gy.demo.mq.mqdemo.model.EventLogContext;
import org.gy.demo.mq.mqdemo.model.EventMessage;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author gy
 */
@Slf4j
public abstract class AbstractEventMessageService<T, R> implements EventMessageService<T, R> {

    @Resource
    private EventMessageSendService eventMessageSendService;

    protected abstract R internalExecute(T data);

    protected abstract Class<T> getDataType();

    protected T convert(EventMessage<T> eventMessage) {
        if (eventMessage == null) {
            log.warn("[EventMessageService]消息数据为空");
            return null;
        }
        try {
            return JSON.to(getDataType(), eventMessage.getData());
        } catch (Exception e) {
            log.error("[EventMessageService]消息数据转换异常, event={}.", eventMessage, e);
            return null;
        }
    }

    protected R internalExecuteWithContext(T data) {
        return doWithContext(data, this::internalExecute);
    }

    @Override
    public R execute(EventMessage<T> eventMessage) {
        T data = convert(eventMessage);
        return internalExecuteWithContext(data);
    }

//    @Override
//    public boolean supportRetry(Throwable ex) {
//        return !(ex instanceof BizException);
//    }


    @Override
    public R synchronousSend(EventMessage<T> req) {
        return EventLogContext.handleWithLog(req, this::internalExecuteWithContext);
    }

    @Override
    public void asyncSendInternal(EventMessage<T> req) {
        this.sendInternal(req);
    }

    @Override
    public void asyncSend(List<EventMessage<T>> reqs) {
        List<EventMessage<T>> eventSendReqs = Optional.ofNullable(reqs).orElseGet(Collections::emptyList);
        eventSendReqs.forEach(this::sendInternal);
    }

    protected void sendInternal(EventMessage<T> item) {
        if (StringUtils.isNotBlank(item.getOrderlyKey())) {
            eventMessageSendService.sendOrderlyMessageAsync(item);
        } else {
            eventMessageSendService.sendNormalMessageAsync(item);
        }
    }

    protected <REQ, RES> RES doWithContext(REQ req, Function<REQ, RES> function) {
        EventMessageServiceManager.setCurrentService(this);
        try {
            return function.apply(req);
        } finally {
            EventMessageServiceManager.clearCurrentService();
        }
    }
}
