package org.gy.demo.kafka.kafkademo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

/**
 * @author guanyang
 */
@Slf4j
@Configuration
public class DemoConfig {

    @Bean
    public DefaultErrorHandler demoErrorHandler() {
        // 配置重试策略：指数退避重试，最多重试 5 次
        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);
        // 初始间隔 1 秒
        backOff.setInitialInterval(1000);
        // 每次重试间隔翻倍
        backOff.setMultiplier(2);
        // 最大间隔 10 秒
        backOff.setMaxInterval(10000);
        // 自定义异常处理逻辑
        ConsumerRecordRecoverer recoverer = (record, ex) -> {
            log.error("消费失败：{}", record.value(), ex);
            // 可以将消息发送到死信队列或记录日志
        };

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);

        // 配置哪些异常不触发重试
//        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);

        return errorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(ConsumerFactory<String, Object> consumerFactory, DefaultErrorHandler errorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // 设置自定义异常处理器
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}
