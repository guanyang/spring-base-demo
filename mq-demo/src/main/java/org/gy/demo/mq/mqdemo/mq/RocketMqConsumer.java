package org.gy.demo.mq.mqdemo.mq;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.gy.demo.mq.mqdemo.mq.RocketMQProperties.Consumer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/12/22 14:20
 */
@Slf4j
@Getter
public class RocketMqConsumer implements InitializingBean, DisposableBean {

    private final RocketMQProperties rocketMQProperties;

    private final MessageListener messageListener;

    private DefaultMQPushConsumer consumer;

    public RocketMqConsumer(final RocketMQProperties rocketMQProperties, final MessageListener messageListener) {
        this.rocketMQProperties = rocketMQProperties;
        this.messageListener = messageListener;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("开始启动RocketMQ消费者服务...");
        String nameServer = rocketMQProperties.getNameServer();
        Assert.hasText(nameServer, "RocketMQProperties.nameServer must not be null");

        String topicName = rocketMQProperties.getTopic();
        Assert.hasText(topicName, "RocketMQProperties.topic must not be null");

        Consumer consumerConfig = rocketMQProperties.getConsumer();
        Assert.notNull(consumerConfig, "RocketMQProperties.consumer must not be null");

        String groupName = consumerConfig.getGroupName();
        Assert.hasText(groupName, "RocketMQProperties.consumer.groupName must not be null");

        consumer = new DefaultMQPushConsumer(groupName);
        consumer.setNamesrvAddr(nameServer);
        //同一个group启动多个消费者，定义不同的名称，避免冲突
        consumer.setInstanceName(consumerConfig.getInstanceName());
        consumer.setNamespace(consumerConfig.getNamespace());
        MessageModel messageModel = MessageModel.valueOf(consumerConfig.getMessageModel());
        //消费方式，CLUSTERING/BROADCASTING
        consumer.setMessageModel(messageModel);
        consumer.subscribe(topicName, consumerConfig.getSelectorExpression());
        consumer.setConsumeMessageBatchMaxSize(consumerConfig.getConsumeMessageBatchMaxSize());
        //最小处理线程数
        consumer.setConsumeThreadMin(consumerConfig.getConsumeThreadMin());
        //最大处理线程数
        consumer.setConsumeThreadMax(consumerConfig.getConsumeThreadMax());

        consumer.registerMessageListener(messageListener);
        ConsumeFromWhere consumeFromWhere = ConsumeFromWhere.valueOf(consumerConfig.getConsumeFromWhere());
        consumer.setConsumeFromWhere(consumeFromWhere);
        consumer.start();
        log.info("RocketMQ消费者启动成功.");
    }


    @Override
    public void destroy() throws Exception {
        if (consumer != null) {
            log.info("开始关闭RocketMQ消费者服务...");
            consumer.shutdown();
            log.info("RocketMQ消费者服务已关闭.");
        }
    }
}
