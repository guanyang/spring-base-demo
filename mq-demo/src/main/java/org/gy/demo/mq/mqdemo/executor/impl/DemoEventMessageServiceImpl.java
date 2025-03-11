package org.gy.demo.mq.mqdemo.executor.impl;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.mq.mqdemo.executor.DemoEventMessageService;
import org.gy.demo.mq.mqdemo.executor.support.DynamicEventStrategy;
import org.gy.demo.mq.mqdemo.executor.support.EventMessageServiceManager;
import org.gy.demo.mq.mqdemo.model.EventMessage;
import org.gy.demo.mq.mqdemo.model.EventType;
import org.gy.framework.core.dto.Response;
import org.gy.framework.core.exception.RetryException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Objects;
import java.util.Optional;

/**
 * 功能描述：示例事件处理
 *
 * @author gy
 */
@Slf4j
@Service
public class DemoEventMessageServiceImpl extends AbstractEventMessageService<String, Response<Object>> implements DemoEventMessageService {

    @Override
    public EventType getEventType() {
        return EventType.DEMO_EVENT;
    }

    @Override
    public Class<String> getDataType() {
        return String.class;
    }

    @Override
    protected String convert(EventMessage<?> event) {
        return Optional.ofNullable(event).map(EventMessage::getData).map(Object::toString).orElse(StrUtil.EMPTY);
    }


    @Override
    protected Response<Object> internalExecute(String data) {
        log.info("[DEMO_EVENT]消息数据:{}", data);
        return Response.asSuccess(data);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @DynamicEventStrategy(eventType = EventType.DYNAMIC_DEMO_EVENT, supportRetry = RetryException.class)
    public void dynamicEventTest(String data) {
        boolean transactionActive = TransactionSynchronizationManager.isActualTransactionActive();
        boolean currentServiceActive = EventMessageServiceManager.isCurrentServiceActive();
        log.info("[DYNAMIC_DEMO_EVENT]消息数据: data={},transactionActive={},currentServiceActive={}", data, transactionActive, currentServiceActive);
        if (Objects.equals(data, "error")) {
            throw new RetryException(500, "测试异常");
        }
    }
}
