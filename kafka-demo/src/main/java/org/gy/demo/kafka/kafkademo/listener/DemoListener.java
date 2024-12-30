package org.gy.demo.kafka.kafkademo.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author guanyang
 */
@Slf4j
@Component
public class DemoListener {

    @KafkaListener(topics = "${spring.kafka.topic}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, Object> record) {
        log.info("简单消费：{}-{}-{}", record.topic(), record.partition(), record.value());
        if (Objects.equals(record.value(), "error")) {
            throw new RuntimeException("测试异常");
        }
    }

}
