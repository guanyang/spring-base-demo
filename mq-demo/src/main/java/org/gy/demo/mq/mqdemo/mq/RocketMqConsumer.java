package org.gy.demo.mq.mqdemo.mq;

import cn.hutool.extra.spring.SpringUtil;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;
import org.gy.demo.mq.mqdemo.mq.RocketMQProperties.Consumer;
import org.gy.demo.mq.mqdemo.mq.RocketMQProperties.RocketMQConfig;
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
public class RocketMqConsumer {

    private final AtomicBoolean init = new AtomicBoolean(false);

    private final RocketMQConfig rocketMQConfig;

    private final String groupName;

    private DefaultMQPushConsumer consumer;


    public RocketMqConsumer(final RocketMQConfig rocketMQConfig) {
        this.rocketMQConfig = checkConfig(rocketMQConfig);
        this.groupName = rocketMQConfig.getConsumer().getGroupName();
    }

    protected RocketMQConfig checkConfig(final RocketMQConfig rocketMQConfig) {
        String nameServer = rocketMQConfig.getNameServer();
        Assert.hasText(nameServer, () -> "RocketMQConfig.nameServer must not be null");

        String topicName = rocketMQConfig.getTopic();
        Assert.hasText(topicName, () -> "RocketMQConfig.topic must not be null");

        Consumer consumerConfig = rocketMQConfig.getConsumer();
        Assert.notNull(consumerConfig, () -> "RocketMQConfig.consumer must not be null");

        String groupName = consumerConfig.getGroupName();
        Assert.hasText(groupName, () -> "RocketMQConfig.consumer.groupName must not be null");

        return rocketMQConfig;
    }

    public void init() throws Exception {
        if (init.compareAndSet(false, true)) {
            log.info("消费者启动开始: {}", groupName);
            consumer = initConsumer(rocketMQConfig);
            consumer.start();
            log.info("消费者启动成功: {}", groupName);
        }
    }

    public void destroy() throws Exception {
        if (init.compareAndSet(true, false)) {
            if (consumer != null) {
                log.info("消费者服务关闭开始: {}", groupName);
                consumer.shutdown();
                log.info("消费者服务关闭成功: {}", groupName);
            }
        }
    }

    protected DefaultMQPushConsumer initConsumer(RocketMQConfig rocketMQConfig) throws Exception {
        Consumer consumerConfig = rocketMQConfig.getConsumer();
        DefaultMQPushConsumer consumer = buildConsumer(consumerConfig);

        consumerCustomize(consumer, rocketMQConfig);
        return consumer;
    }

    protected DefaultMQPushConsumer buildConsumer(Consumer consumerConfig) {
        MessageListener messageListener = getMessageListener(consumerConfig);
        DefaultMQPushConsumer pushConsumer = new DefaultMQPushConsumer(consumerConfig.getGroupName());
        pushConsumer.registerMessageListener(messageListener);
        return pushConsumer;
    }

    protected void consumerCustomize(DefaultMQPushConsumer consumer, RocketMQConfig rocketMQConfig) throws Exception {
        Consumer consumerConfig = rocketMQConfig.getConsumer();
        consumer.setNamesrvAddr(rocketMQConfig.getNameServer());
        //同一个group启动多个消费者，定义不同的名称，避免冲突
        consumer.setInstanceName(consumerConfig.getInstanceName());
        consumer.setNamespace(consumerConfig.getNamespace());
        MessageModel messageModel = MessageModel.valueOf(consumerConfig.getMessageModel());
        //消费方式，CLUSTERING/BROADCASTING
        consumer.setMessageModel(messageModel);
        consumer.subscribe(rocketMQConfig.getTopic(), consumerConfig.getSelectorExpression());
        consumer.setConsumeMessageBatchMaxSize(consumerConfig.getConsumeMessageBatchMaxSize());
        //最小处理线程数
        consumer.setConsumeThreadMin(consumerConfig.getConsumeThreadMin());
        //最大处理线程数
        consumer.setConsumeThreadMax(consumerConfig.getConsumeThreadMax());

        ConsumeFromWhere consumeFromWhere = ConsumeFromWhere.valueOf(consumerConfig.getConsumeFromWhere());
        consumer.setConsumeFromWhere(consumeFromWhere);
        consumer.registerConsumeMessageHook(new TraceConsumeMessageHook());
    }

    protected MessageListener getMessageListener(Consumer consumerConfig) {
        String beanName = consumerConfig.getMessageListenerBeanName();
        Assert.hasText(beanName, () -> "RocketMQConfig.consumer.messageListenerBeanName must not be null");
        MessageListener listener = SpringUtil.getBean(beanName, MessageListener.class);
        Assert.notNull(listener, () -> "MessageListener must not be null: " + beanName);
        return listener;
    }
}
