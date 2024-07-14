package org.gy.demo.mq.mqdemo.executor.impl;

import static org.gy.framework.core.exception.CommonErrorCode.PARAM_ERROR;
import static org.gy.framework.core.exception.CommonErrorCode.SYS_SERVICE_ERROR;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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

    private RocketMqProducer getProducer(String name) {
        return rocketMqManager.getProducer(name);
    }

    private <T> SendResult send(RocketMqProducer producer, EventMessage<T> eventMessage, boolean async) {
        BiFunctionAction<Message, SendCallback, SendResult> function = (message, sendCallback) -> {
            return send(producer.getProducer(), message, sendCallback, eventMessage.getOrderlyKey());
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

    private <T> SendResult send(DefaultMQProducer defaultProducer, Message msg, SendCallback callback,
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
        return new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("[EventMessageSendService]发送成功：event={},result={}", eventMessage, sendResult);
            }

            @Override
            public void onException(Throwable e) {
                //仅告警处理，后续考虑写入异常表，进行异步补偿处理
                log.error("[EventMessageSendService]发送失败：event={}.", eventMessage, e);
            }
        };
    }
}
