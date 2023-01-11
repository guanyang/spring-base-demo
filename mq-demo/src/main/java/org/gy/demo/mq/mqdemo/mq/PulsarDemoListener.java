package org.gy.demo.mq.mqdemo.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.MessageListener;
import org.springframework.stereotype.Component;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2023/1/11 10:21
 */
@Slf4j
@Component("pulsarDemoListener")
public class PulsarDemoListener implements MessageListener<String> {

    private static final long serialVersionUID = 8265167947213544128L;

    @Override
    public void received(Consumer<String> consumer, Message<String> msg) {
        try {
            String topic = msg.getTopicName();
            MessageId messageId = msg.getMessageId();
            String msgBody = msg.getValue();
            log.info("收到消息:topic={},body={},messageId={}", topic, msgBody, messageId);
            consumer.acknowledge(msg);
        } catch (Exception e) {
            log.error("PulsarDemoListener exception.", e);
            consumer.negativeAcknowledge(msg);
        }
    }
}
