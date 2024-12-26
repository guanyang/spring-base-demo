package org.gy.demo.mq.mqdemo.executor.impl;

import lombok.extern.slf4j.Slf4j;
import org.gy.demo.mq.mqdemo.executor.EventMessageDispatchService;
import org.gy.demo.mq.mqdemo.executor.EventMessageService;
import org.gy.demo.mq.mqdemo.executor.support.EventMessageServiceManager;
import org.gy.demo.mq.mqdemo.model.EventMessage;
import org.gy.demo.mq.mqdemo.model.EventMessageDispatchResult;
import org.gy.framework.core.exception.BizException;
import org.springframework.stereotype.Service;

/**
 * @author gy
 */
@Slf4j
@Service
public class EventMessageDispatchServiceImpl implements EventMessageDispatchService {

    @Override
    public EventMessageDispatchResult execute(EventMessage<?> eventMessage) {
        if (eventMessage == null || eventMessage.getEventType() == null) {
            log.warn("[EventMessageDispatchService]消息数据为空, event={}", eventMessage);
            return EventMessageDispatchResult.of(new BizException(400, "Event data is empty!"));
        }
        EventMessageService actionService = EventMessageServiceManager.getService(eventMessage.getEventType()).orElse(null);
        if (actionService == null) {
            log.warn("[EventMessageDispatchService]消息事件服务不存在:event={}", eventMessage);
            return EventMessageDispatchResult.of(new BizException(400, "Event service is empty!"));
        }

        EventMessageDispatchResult response = new EventMessageDispatchResult();
        response.setService(actionService);
        try {
            Object result = actionService.execute(eventMessage);
            response.setResult(result);
        } catch (Throwable e) {
            response.setEx(e);
        }
        return response;
    }
}
