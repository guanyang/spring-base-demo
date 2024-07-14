package org.gy.demo.mq.mqdemo.executor;


import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.gy.demo.mq.mqdemo.model.EventMessage;
import org.gy.demo.mq.mqdemo.mq.RocketMqProducer;

/**
 * @author gy
 */
public interface EventMessageSendService {

    <T> SendResult send(RocketMqProducer producer, EventMessage<T> eventMessage);

    <T> SendResult sendNormalMessage(EventMessage<T> eventMessage);

    <T> SendResult sendOrderlyMessage(EventMessage<T> eventMessage);

    <T> TransactionSendResult sendTransactionMessage(RocketMqProducer producer, EventMessage<T> eventMessage);

    <T> TransactionSendResult sendTransactionMessage(EventMessage<T> eventMessage);

    <T> void sendAsync(RocketMqProducer producer, EventMessage<T> eventMessage);

    <T> void sendNormalMessageAsync(EventMessage<T> eventMessage);

    <T> void sendOrderlyMessageAsync(EventMessage<T> eventMessage);

}
