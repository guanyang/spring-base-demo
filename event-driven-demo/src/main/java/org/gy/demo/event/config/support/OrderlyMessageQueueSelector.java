package org.gy.demo.event.config.support;

import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageQueue;
import org.springframework.messaging.MessageHeaders;

import java.util.List;

public class OrderlyMessageQueueSelector implements MessageQueueSelector {
    @Override
    public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
        Object keyArg = ((MessageHeaders) arg).get(MessageConst.PROPERTY_KEYS);
        int value = Math.abs(keyArg.hashCode());
        int index = value % mqs.size();
        return mqs.get(index);
    }
}
