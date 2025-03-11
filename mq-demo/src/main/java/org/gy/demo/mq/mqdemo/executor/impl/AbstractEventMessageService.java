package org.gy.demo.mq.mqdemo.executor.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author gy
 */
@Slf4j
public abstract class AbstractEventMessageService<T, R> implements EventMessageService<T, R> {

    @Resource
    private EventMessageSendService eventMessageSendService;

    protected abstract R internalExecute(T data);

    protected abstract Class<T> getDataType();

    protected T convert(EventMessage<?> eventMessage) {
        if (eventMessage == null || eventMessage.getData() == null) {
            log.warn("[EventMessageService]消息数据为空");
            return null;
        }
        try {
            Class<T> dataType = getDataType();
            if (dataType.isInstance(eventMessage.getData())) {
                return (T) eventMessage.getData();
            }
            return JSON.to(dataType, eventMessage.getData());
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
        return EventLogContext.handleWithLog(req, this::execute);
    }

    @Override
    public void asyncSendInternal(EventMessage<T> req) {
        Optional.ofNullable(req).ifPresent(item -> sendInternal(Lists.newArrayList(item)));
    }

    @Override
    public void asyncSend(List<EventMessage<T>> reqs) {
        sendInternal(reqs);
    }

    protected void sendInternal(List<EventMessage<T>> reqs) {
        List<EventMessage<T>> eventMessages = Optional.ofNullable(reqs).orElseGet(Collections::emptyList).stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(eventMessages)) {
            return;
        }
        boolean supportBatch = eventMessages.stream().allMatch(this::supportBatch);
        if (supportBatch) {
            //批量发送普通消息（批量消息不支持延迟、顺序消费）
            eventMessageSendService.sendNormalMessageAsync(eventMessages);
            return;
        }
        eventMessages.forEach(item -> {
            if (StringUtils.isNotBlank(item.getOrderlyKey())) {
                eventMessageSendService.sendOrderlyMessageAsync(item);
            } else {
                eventMessageSendService.sendNormalMessageAsync(item);
            }
        });
    }

    protected <T> boolean supportBatch(EventMessage<T> item) {
        //批量消息不支持延迟、顺序消费
        return StringUtils.isBlank(item.getOrderlyKey()) && item.getDelayTimeLevel() == 0;
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
