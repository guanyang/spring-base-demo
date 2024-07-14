package org.gy.demo.mq.mqdemo.mq;

import static org.gy.demo.mq.mqdemo.mq.DefaultMQProducerWrapper.wrapTrace;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;

/**
 * @author gy
 */
public class TransactionMQProducerWrapper extends TransactionMQProducer {


    public TransactionMQProducerWrapper(String producerGroup) {
        super(producerGroup);
    }

    @Override
    public TransactionSendResult sendMessageInTransaction(Message msg, Object arg) throws MQClientException {
        return super.sendMessageInTransaction(wrapTrace(msg), arg);
    }

}
