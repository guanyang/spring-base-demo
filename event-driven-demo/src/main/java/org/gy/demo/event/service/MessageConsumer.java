package org.gy.demo.event.service;

import com.alibaba.cloud.stream.binder.rocketmq.support.RocketMQMessageConverterSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.gy.demo.event.model.Log;
import org.gy.demo.event.model.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class MessageConsumer {

    // 普通消息消费者
    @Bean
    public Consumer<Log> consumer() {
        return message -> log.info("Normal Consumer Received: {}", message);
    }

    // 顺序消息消费者
    @Bean
    public Consumer<Message<Order>> orderlyConsumer() {
        return message -> {
            String tagHeaderKey = RocketMQMessageConverterSupport.toRocketHeaderKey(MessageConst.PROPERTY_TAGS);
            Order msg = message.getPayload();
            String tags = message.getHeaders().get(tagHeaderKey, String.class);
            log.info("Receive Orderly Messages: msg={} tags={}", msg, tags);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        };
    }
}
