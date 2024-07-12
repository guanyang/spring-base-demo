package org.gy.demo.mq.mqdemo.executor.impl;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.mq.mqdemo.executor.EventMessageDispatchService;
import org.gy.demo.mq.mqdemo.executor.EventMessageService;
import org.gy.demo.mq.mqdemo.model.EventStringMessage;
import org.gy.demo.mq.mqdemo.model.EventType;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * @author gy
 */
@Slf4j
@Service
public class EventMessageDispatchServiceImpl implements EventMessageDispatchService, InitializingBean {

    private final Map<EventType, EventMessageService> serviceMap = Maps.newConcurrentMap();

    @Resource
    private List<EventMessageService> eventMessageServices;

    @Override
    public void execute(EventStringMessage eventMessage) {
        if (eventMessage == null || StrUtil.isBlank(eventMessage.getData())) {
            log.warn("[EventMessageDispatchService]消息数据为空, event={}", eventMessage);
            return;
        }
        EventMessageService actionService = Optional.ofNullable(eventMessage.getEventType()).map(serviceMap::get)
            .orElse(null);
        if (actionService == null) {
            log.warn("[EventMessageDispatchService]消息事件服务不存在:event={}", eventMessage);
            return;
        }

        try {
            actionService.execute(eventMessage);
        } catch (Throwable e) {
            //常规业务异常不做重试处理，其他异常重试
            if (actionService.supportRetry(e)) {
                throw e;
            } else {
                log.warn("[EventMessageDispatchService]业务非重试异常: event={}.", eventMessage, e);
            }
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (EventMessageService actionService : eventMessageServices) {
            serviceMap.put(actionService.getEventType(), actionService);
        }
    }
}
