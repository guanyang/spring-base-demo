package org.gy.demo.mq.mqdemo.mq;

import lombok.extern.slf4j.Slf4j;
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

//    @Bean(name = "demoConsumer")
//    public RocketMqConsumer demoConsumer(@Qualifier("demoProperties") RocketMQProperties demoProperties,
//        @Qualifier("demoListener") MessageListener demoListener) {
//        return new RocketMqConsumer(demoProperties, demoListener);
//    }

}
