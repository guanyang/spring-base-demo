package org.gy.demo.mq.mqdemo.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/12/22 14:53
 */
@Slf4j
@Configuration
public class RocketMqConfiguration {

    @Bean(name = "demoProperties")
    @ConfigurationProperties(prefix = "rocketmq.demo")
    public RocketMQProperties demoProperties() {
        return new RocketMQProperties();
    }


    @Bean(name = "demoProducer")
    public RocketMqProducer demoProducer(@Qualifier("demoProperties") RocketMQProperties demoProperties) {
        return new RocketMqProducer(demoProperties);
    }

    @Bean(name = "demoConsumer")
    public RocketMqConsumer demoConsumer(@Qualifier("demoProperties") RocketMQProperties demoProperties,
        @Qualifier("demoListener") MessageListener demoListener) {
        return new RocketMqConsumer(demoProperties, demoListener);
    }

    @Bean(name = "orderlyProperties")
    @ConfigurationProperties(prefix = "rocketmq.orderly")
    public RocketMQProperties orderlyProperties() {
        return new RocketMQProperties();
    }


    @Bean(name = "orderlyProducer")
    public RocketMqProducer orderlyProducer(@Qualifier("orderlyProperties") RocketMQProperties orderlyProperties) {
        return new RocketMqProducer(orderlyProperties);
    }

    @Bean(name = "orderlyConsumer")
    public RocketMqConsumer orderlyConsumer(@Qualifier("orderlyProperties") RocketMQProperties orderlyProperties,
        @Qualifier("orderlyListener") MessageListener orderlyListener) {
        return new RocketMqConsumer(orderlyProperties, orderlyListener);
    }

}
