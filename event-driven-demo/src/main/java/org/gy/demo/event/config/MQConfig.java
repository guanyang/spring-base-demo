package org.gy.demo.event.config;

import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.gy.demo.event.config.support.OrderlyMessageQueueSelector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

    @Bean
    @ConditionalOnMissingBean(MessageQueueSelector.class)
    public MessageQueueSelector orderlyMessageQueueSelector() {
        return new OrderlyMessageQueueSelector();
    }
}
