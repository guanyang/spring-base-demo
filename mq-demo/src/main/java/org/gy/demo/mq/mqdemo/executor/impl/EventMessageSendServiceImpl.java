package org.gy.demo.mq.mqdemo.executor.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.client.producer.selector.SelectMessageQueueByHash;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.gy.demo.mq.mqdemo.common.Action.BiFunctionAction;
import org.gy.demo.mq.mqdemo.executor.EventMessageSendService;
import org.gy.demo.mq.mqdemo.model.EventMessage;
import org.gy.demo.mq.mqdemo.mq.RocketMQProperties.RocketMQConfig;
import org.gy.demo.mq.mqdemo.mq.RocketMqConfiguration.RocketMqManager;
import org.gy.demo.mq.mqdemo.mq.RocketMqProducer;
import org.gy.framework.core.exception.BizException;
import org.gy.framework.core.exception.SysException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.gy.framework.core.exception.CommonErrorCode.PARAM_ERROR;
import static org.gy.framework.core.exception.CommonErrorCode.SYS_SERVICE_ERROR;

/**
 * @author gy
 */
@Slf4j
@Service
public class EventMessageSendServiceImpl implements EventMessageSendService {

    public static final String ORDERLY_PRODUCER = "orderly";
    public static final String DEMO_PRODUCER = "demo";
    public static final String TRANSACTION_PRODUCER = "demoTransaction";

    @Resource
    private RocketMqManager rocketMqManager;

    @Override
    public <T> SendResult send(RocketMqProducer producer, EventMessage<T> eventMessage) {
        return send(producer, eventMessage, false);
    }

    @Override
    public <T> SendResult sendNormalMessage(EventMessage<T> eventMessage) {
        return send(getProducer(DEMO_PRODUCER), eventMessage);
    }

    @Override
    public <T> SendResult sendOrderlyMessage(EventMessage<T> eventMessage) {
        return send(getProducer(ORDERLY_PRODUCER), eventMessage);
    }

    @Override
    public <T> TransactionSendResult sendTransactionMessage(RocketMqProducer producer, EventMessage<T> eventMessage) {
        BiFunctionAction<Message, SendCallback, TransactionSendResult> function = (message, sendCallback) -> {
            return producer.getProducer().sendMessageInTransaction(message, eventMessage);
        };
        // 发送事务消息，不支持异步
        return send(producer, eventMessage, false, function);
    }

    @Override
    public <T> TransactionSendResult sendTransactionMessage(EventMessage<T> eventMessage) {
        RocketMqProducer producer = getProducer(TRANSACTION_PRODUCER);
        return sendTransactionMessage(producer, eventMessage);
    }

    @Override
    public <T> void sendAsync(RocketMqProducer producer, EventMessage<T> eventMessage) {
        send(producer, eventMessage, true);
    }

    @Override
    public <T> void sendNormalMessageAsync(EventMessage<T> eventMessage) {
        sendAsync(getProducer(DEMO_PRODUCER), eventMessage);
    }

    @Override
    public <T> void sendOrderlyMessageAsync(EventMessage<T> eventMessage) {
        sendAsync(getProducer(ORDERLY_PRODUCER), eventMessage);
    }

    @Override
    public <T> void sendNormalMessageAsync(List<EventMessage<T>> eventMessages) {
        sendInternalAsync(getProducer(DEMO_PRODUCER), eventMessages);
    }

    private RocketMqProducer getProducer(String name) {
        return rocketMqManager.getProducer(name);
    }

    private <T> SendResult send(RocketMqProducer producer, EventMessage<T> eventMessage, boolean async) {
        BiFunctionAction<Message, SendCallback, SendResult> function = (message, sendCallback) -> {
            return sendInternal(producer.getProducer(), message, sendCallback, eventMessage.getOrderlyKey());
        };
        return send(producer, eventMessage, async, function);
    }

    private <T, R> R send(RocketMqProducer producer, EventMessage<T> eventMessage, boolean async,
                          BiFunctionAction<Message, SendCallback, R> function) {
        if (eventMessage == null || producer == null) {
            log.warn("[EventMessageSendService]参数为空：event={}", eventMessage);
            throw new BizException(PARAM_ERROR);
        }
        RocketMQConfig config = producer.getRocketMQConfig();
        try {
            Message msg = buildMsg(eventMessage, config.getTopic());
            SendCallback callback = async ? buildCallback(eventMessage) : null;

            return function.apply(msg, callback);
        } catch (Exception e) {
            //仅告警处理，后续考虑写入异常表，进行异步补偿处理
            log.error("[EventMessageSendService]发送异常：event={}.", eventMessage, e);
            throw new SysException(SYS_SERVICE_ERROR);
        }
    }

    private <T> void sendInternalAsync(RocketMqProducer producer, List<EventMessage<T>> eventMessages) {
        if (CollectionUtils.isEmpty(eventMessages) || producer == null) {
            log.warn("[EventMessageSendService]参数错误");
            return;
        }
        try {
            sendInternal(producer, eventMessages, true);
        } catch (Exception e) {
            //仅告警处理，后续考虑写入异常表，进行异步补偿处理
            log.error("[EventMessageSendService]发送异常", e);
            throw new SysException(SYS_SERVICE_ERROR);
        }
    }

    private <T> SendResult sendInternal(RocketMqProducer rocketMqProducer, List<EventMessage<T>> eventMessages, boolean async) throws Exception {
        RocketMQConfig config = rocketMqProducer.getRocketMQConfig();
        DefaultMQProducer defaultProducer = rocketMqProducer.getProducer();
        SendCallback callback = async ? buildCallback(eventMessages) : null;
        //由于rocketmq批量消息不支持有序，不支持延迟，需要单独处理
        if (eventMessages.size() > 1) {
            List<Message> msgList = buildMsg(eventMessages, config.getTopic());
            return sendInternal(defaultProducer, msgList, callback);
        } else {
            EventMessage<T> eventMessage = eventMessages.get(0);
            Message message = buildMsg(eventMessage, config.getTopic());
            return sendInternal(defaultProducer, message, callback, eventMessage.getOrderlyKey());
        }
    }

    private SendResult sendInternal(DefaultMQProducer defaultProducer, Collection<Message> msgList, SendCallback callback) throws Exception {
        if (callback == null) {
            return defaultProducer.send(msgList);
        }
        defaultProducer.send(msgList, callback);
        return null;
    }

    private SendResult sendInternal(DefaultMQProducer defaultProducer, Message msg, SendCallback callback,
                                String orderlyKey) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        if (callback == null) {
            if (StrUtil.isNotBlank(orderlyKey)) {
                return defaultProducer.send(msg, new SelectMessageQueueByHash(), orderlyKey);
            } else {
                return defaultProducer.send(msg);
            }
        } else {
            if (StrUtil.isNotBlank(orderlyKey)) {
                defaultProducer.send(msg, new SelectMessageQueueByHash(), orderlyKey, callback);
            } else {
                defaultProducer.send(msg, callback);
            }
            return null;
        }
    }

    private <T> List<Message> buildMsg(Collection<EventMessage<T>> eventMessages, String topic) {
        return eventMessages.stream().map(eventMessage -> buildMsg(eventMessage, topic)).collect(Collectors.toList());
    }


    private <T> Message buildMsg(EventMessage<T> eventMessage, String topic) {
        Message msg = new Message();
        msg.setTopic(topic);
        msg.setBody(JSON.toJSONBytes(eventMessage));
        if (eventMessage.getDelayTimeLevel() > 0) {
            msg.setDelayTimeLevel(eventMessage.getDelayTimeLevel());
        }
        return msg;
    }

    private <T> SendCallback buildCallback(EventMessage<T> eventMessage) {
        return buildCallback(Lists.newArrayList(eventMessage));
    }

    private <T> SendCallback buildCallback(Collection<EventMessage<T>> eventMessages) {
        return new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("[EventMessageSendService]发送成功：result={}", sendResult);
            }

            @Override
            public void onException(Throwable e) {
                //仅告警处理，后续考虑写入异常表，进行异步补偿处理
                log.error("[EventMessageSendService]发送失败", e);
            }
        };
    }
}
