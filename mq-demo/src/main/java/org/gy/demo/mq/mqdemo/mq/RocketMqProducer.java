package org.gy.demo.mq.mqdemo.mq;

import cn.hutool.extra.spring.SpringUtil;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.gy.demo.mq.mqdemo.mq.RocketMQProperties.Producer;
import org.gy.demo.mq.mqdemo.mq.RocketMQProperties.ProducerType;
import org.gy.demo.mq.mqdemo.mq.RocketMQProperties.RocketMQConfig;
import org.springframework.util.Assert;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/12/22 13:31
 */
@Slf4j
@Getter
public class RocketMqProducer {

    private final AtomicBoolean init = new AtomicBoolean(false);

    private final RocketMQConfig rocketMQConfig;

    private final String groupName;

    private DefaultMQProducer producer;

    public RocketMqProducer(final RocketMQConfig rocketMQConfig) {
        this.rocketMQConfig = checkConfig(rocketMQConfig);
        this.groupName = rocketMQConfig.getProducer().getGroupName();
    }

    protected RocketMQConfig checkConfig(final RocketMQConfig rocketMQConfig) {
        String nameServer = rocketMQConfig.getNameServer();
        Assert.hasText(nameServer, () -> "RocketMQConfig.nameServer must not be null");

        String topicName = rocketMQConfig.getTopic();
        Assert.hasText(topicName, () -> "RocketMQConfig.topic must not be null");

        Producer producerConfig = rocketMQConfig.getProducer();
        Assert.notNull(producerConfig, () -> "RocketMQConfig.producer must not be null");

        String groupName = producerConfig.getGroupName();
        Assert.hasText(groupName, () -> "RocketMQConfig.producer.groupName must not be null");

        return rocketMQConfig;
    }


    public void init() throws Exception {
        if (init.compareAndSet(false, true)) {
            log.info("生产者启动开始: {}", groupName);
            producer = initProducer(rocketMQConfig);
            producer.start();
            log.info("生产者启动成功: {}", groupName);
        }
    }

    public void destroy() throws Exception {
        if (init.compareAndSet(true, false)) {
            if (producer != null) {
                log.info("生产者服务关闭开始: {}", groupName);
                producer.shutdown();
                log.info("生产者服务关闭成功: {}", groupName);
            }
        }
    }

    protected DefaultMQProducer initProducer(RocketMQConfig rocketMQConfig) {
        Producer producerConfig = rocketMQConfig.getProducer();

        ProducerType producerType = producerConfig.getProducerType();
        DefaultMQProducer producerWrapper;
        if (ProducerType.TRANSACTION.equals(producerType)) {
            producerWrapper = buildTransactionProducer(producerConfig);
        } else {
            producerWrapper = buildNormalProducer(producerConfig);
        }
        producerCustomize(producerWrapper, rocketMQConfig);
        return producerWrapper;
    }

    protected void producerCustomize(DefaultMQProducer producerWrapper, RocketMQConfig rocketMQConfig) {
        Producer producerConfig = rocketMQConfig.getProducer();
        producerWrapper.setNamesrvAddr(rocketMQConfig.getNameServer());
        //同一个group定义多个实例，需要定义不同的实例名称，避免冲突
        producerWrapper.setInstanceName(producerConfig.getInstanceName());
        producerWrapper.setNamespace(producerConfig.getNamespace());
        producerWrapper.setSendMsgTimeout(producerConfig.getSendMessageTimeout());
        producerWrapper.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
        producerWrapper.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
        producerWrapper.setMaxMessageSize(producerConfig.getMaxMessageSize());
        producerWrapper.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMessageBodyThreshold());
        producerWrapper.setRetryAnotherBrokerWhenNotStoreOK(producerConfig.isRetryNextServer());
    }

    protected DefaultMQProducer buildNormalProducer(Producer producerConfig) {
        return new DefaultMQProducerWrapper(producerConfig.getGroupName());
    }

    protected DefaultMQProducer buildTransactionProducer(Producer producerConfig) {
        TransactionListener transactionListener = getCheckListener(producerConfig);
        TransactionMQProducerWrapper transactionProducer = new TransactionMQProducerWrapper(
            producerConfig.getGroupName());
        transactionProducer.setTransactionListener(transactionListener);
        return transactionProducer;
    }

    protected TransactionListener getCheckListener(Producer producerConfig) {
        String beanName = producerConfig.getTransactionListenerBeanName();
        Assert.hasText(beanName, () -> "RocketMQConfig.producer.transactionListenerBeanName must not be null");
        TransactionListener listener = SpringUtil.getBean(beanName, TransactionListener.class);
        Assert.notNull(listener, () -> "TransactionListener must not be null: " + beanName);
        return listener;
    }

}
