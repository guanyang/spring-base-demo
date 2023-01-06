package org.gy.demo.mq.mqdemo.mq;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.gy.demo.mq.mqdemo.mq.RocketMQProperties.Producer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
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
public class RocketMqProducer implements InitializingBean, DisposableBean {

    private final RocketMQProperties rocketMQProperties;

    private DefaultMQProducer producer;

    public RocketMqProducer(final RocketMQProperties rocketMQProperties) {
        this.rocketMQProperties = rocketMQProperties;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("开始启动RocketMQ生产者服务...");
        String nameServer = rocketMQProperties.getNameServer();
        Assert.hasText(nameServer, "RocketMQProperties.nameServer must not be null");

        String topicName = rocketMQProperties.getTopic();
        Assert.hasText(topicName, "RocketMQProperties.topic must not be null");

        Producer producerConfig = rocketMQProperties.getProducer();
        Assert.notNull(producerConfig, "RocketMQProperties.producer must not be null");

        String groupName = producerConfig.getGroupName();
        Assert.hasText(groupName, "RocketMQProperties.producer.groupName must not be null");

        producer = new DefaultMQProducer(groupName);
        producer.setNamesrvAddr(nameServer);
        //同一个group定义多个实例，需要定义不同的实例名称，避免冲突
        producer.setInstanceName(producerConfig.getInstanceName());
        producer.setNamespace(producerConfig.getNamespace());
        producer.setSendMsgTimeout(producerConfig.getSendMessageTimeout());
        producer.setRetryTimesWhenSendFailed(producerConfig.getRetryTimesWhenSendFailed());
        producer.setRetryTimesWhenSendAsyncFailed(producerConfig.getRetryTimesWhenSendAsyncFailed());
        producer.setMaxMessageSize(producerConfig.getMaxMessageSize());
        producer.setCompressMsgBodyOverHowmuch(producerConfig.getCompressMessageBodyThreshold());
        producer.setRetryAnotherBrokerWhenNotStoreOK(producerConfig.isRetryNextServer());
        producer.start();
        log.info("RocketMQ生产者启动成功.");

    }

    @Override
    public void destroy() throws Exception {
        if (producer != null) {
            log.info("开始关闭RocketMQ生产者服务...");
            producer.shutdown();
            log.info("RocketMQ生产者服务已关闭.");
        }
    }

}
