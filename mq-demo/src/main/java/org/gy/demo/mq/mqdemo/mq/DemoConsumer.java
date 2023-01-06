package org.gy.demo.mq.mqdemo.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/12/22 19:03
 */
@Slf4j
@Service
@RocketMQMessageListener(nameServer = "${rocketmq.demo.nameServer}", topic = "${rocketmq.demo.topic}", selectorExpression = "${rocketmq.demo.consumer.selectorExpression}", consumerGroup = "${rocketmq.demo.consumer.groupName}")
public class DemoConsumer implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt msg) {
        String topic = msg.getTopic();
        String msgBody = new String(msg.getBody());
        String tags = msg.getTags();
        log.info("收到消息:topic={},tags={},body={}", topic, tags, msgBody);
    }
}
