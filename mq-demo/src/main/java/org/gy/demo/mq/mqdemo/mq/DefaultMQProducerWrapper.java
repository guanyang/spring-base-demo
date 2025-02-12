package org.gy.demo.mq.mqdemo.mq;

import brave.Span;
import brave.Tracing;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.gy.demo.mq.mqdemo.mq.support.MessageTraceUtils;
import org.gy.demo.mq.mqdemo.trace.TraceEnum;
import org.slf4j.MDC;

import java.util.Collection;
import java.util.Optional;

/**
 * @author gy
 */
public class DefaultMQProducerWrapper extends DefaultMQProducer {


    public DefaultMQProducerWrapper(String producerGroup) {
        super(producerGroup);
    }

    @Override
    public SendResult send(Message msg)
            throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return super.send(wrapTrace(msg));
    }

    @Override
    public SendResult send(Message msg, long timeout)
            throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return super.send(wrapTrace(msg), timeout);
    }

    @Override
    public void send(Message msg, SendCallback sendCallback)
            throws MQClientException, RemotingException, InterruptedException {
        super.send(wrapTrace(msg), wrapTrace(sendCallback));
    }

    @Override
    public void send(Message msg, SendCallback sendCallback, long timeout)
            throws MQClientException, RemotingException, InterruptedException {
        super.send(wrapTrace(msg), wrapTrace(sendCallback), timeout);
    }

    @Override
    public void sendOneway(Message msg) throws MQClientException, RemotingException, InterruptedException {
        super.sendOneway(wrapTrace(msg));
    }

    @Override
    public SendResult send(Message msg, MessageQueue mq)
            throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return super.send(wrapTrace(msg), mq);
    }

    @Override
    public SendResult send(Message msg, MessageQueue mq, long timeout)
            throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return super.send(wrapTrace(msg), mq, timeout);
    }

    @Override
    public void send(Message msg, MessageQueue mq, SendCallback sendCallback)
            throws MQClientException, RemotingException, InterruptedException {
        super.send(wrapTrace(msg), mq, wrapTrace(sendCallback));
    }

    @Override
    public void send(Message msg, MessageQueue mq, SendCallback sendCallback, long timeout)
            throws MQClientException, RemotingException, InterruptedException {
        super.send(wrapTrace(msg), mq, wrapTrace(sendCallback), timeout);
    }

    @Override
    public void sendOneway(Message msg, MessageQueue mq)
            throws MQClientException, RemotingException, InterruptedException {
        super.sendOneway(wrapTrace(msg), mq);
    }

    @Override
    public SendResult send(Message msg, MessageQueueSelector selector, Object arg)
            throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return super.send(wrapTrace(msg), selector, arg);
    }

    @Override
    public SendResult send(Message msg, MessageQueueSelector selector, Object arg, long timeout)
            throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return super.send(wrapTrace(msg), selector, arg, timeout);
    }

    @Override
    public void send(Message msg, MessageQueueSelector selector, Object arg, SendCallback sendCallback)
            throws MQClientException, RemotingException, InterruptedException {
        super.send(wrapTrace(msg), selector, arg, wrapTrace(sendCallback));
    }

    @Override
    public void send(Message msg, MessageQueueSelector selector, Object arg, SendCallback sendCallback, long timeout)
            throws MQClientException, RemotingException, InterruptedException {
        super.send(wrapTrace(msg), selector, arg, wrapTrace(sendCallback), timeout);
    }

    @Override
    public SendResult sendDirect(Message msg, MessageQueue mq, SendCallback sendCallback)
            throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        return super.sendDirect(wrapTrace(msg), mq, wrapTrace(sendCallback));
    }

    @Override
    public SendResult sendByAccumulator(Message msg, MessageQueue mq, SendCallback sendCallback)
            throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        return super.sendByAccumulator(wrapTrace(msg), mq, wrapTrace(sendCallback));
    }

    @Override
    public void sendOneway(Message msg, MessageQueueSelector selector, Object arg)
            throws MQClientException, RemotingException, InterruptedException {
        super.sendOneway(wrapTrace(msg), selector, arg);
    }

    @Override
    public SendResult send(Collection<Message> msgs)
            throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return super.send(wrapTrace(msgs));
    }

    @Override
    public SendResult send(Collection<Message> msgs, long timeout)
            throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return super.send(wrapTrace(msgs), timeout);
    }

    @Override
    public SendResult send(Collection<Message> msgs, MessageQueue messageQueue)
            throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return super.send(wrapTrace(msgs), messageQueue);
    }

    @Override
    public void send(Collection<Message> msgs, SendCallback sendCallback, long timeout)
            throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        super.send(wrapTrace(msgs), wrapTrace(sendCallback), timeout);
    }

    @Override
    public SendResult send(Collection<Message> msgs, MessageQueue messageQueue, long timeout)
            throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return super.send(wrapTrace(msgs), messageQueue, timeout);
    }

    @Override
    public void send(Collection<Message> msgs, SendCallback sendCallback)
            throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        super.send(wrapTrace(msgs), wrapTrace(sendCallback));
    }

    @Override
    public void send(Collection<Message> msgs, MessageQueue mq, SendCallback sendCallback)
            throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        super.send(wrapTrace(msgs), mq, wrapTrace(sendCallback));
    }

    @Override
    public void send(Collection<Message> msgs, MessageQueue mq, SendCallback sendCallback, long timeout)
            throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        super.send(wrapTrace(msgs), mq, wrapTrace(sendCallback), timeout);
    }

    private static Collection<Message> wrapTrace(Collection<Message> msgs) {
        if (msgs == null) {
            return null;
        }
        msgs.forEach(DefaultMQProducerWrapper::wrapTrace);
        return msgs;
    }

    private static SendCallback wrapTrace(SendCallback sendCallback) {
        if (sendCallback == null) {
            return null;
        }
        if (sendCallback instanceof SendCallbackWrapper) {
            return sendCallback;
        }
        return SendCallbackWrapper.of(sendCallback);
    }

    public static Message wrapTrace(Message msg) {
        if (msg == null) {
            return null;
        }
//        wrapMessage(msg, TraceEnum.TRACE);
//        wrapMessage(msg, TraceEnum.SPAN);
        MessageTraceUtils.wrapTrace(msg);
        return msg;
    }

    private static void wrapMessage(Message msg, TraceEnum keyEnum) {
        String name = keyEnum.getName();
        String value = msg.getUserProperty(name);
        if (value == null) {
            Optional.ofNullable(MDC.get(name)).ifPresent(v -> msg.putUserProperty(name, v));
        }
    }
}
