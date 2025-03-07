package org.gy.demo.event.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageConst;
import org.gy.demo.event.model.Log;
import org.gy.demo.event.model.Order;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.function.Supplier;

@Slf4j
@Configuration
public class MessageProducer {

    public static final String ORDER_BINDINGNAME = "orderlyProducer-out-0";
    public static final String NORMAL_BINDINGNAME = "producer-out-0";

    private final StreamBridge streamBridge;

    public MessageProducer(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void sendOrderMessage(Order order) {
        Message<Order> msg = MessageBuilder.withPayload(order)
                .setHeader(MessageConst.PROPERTY_KEYS, order.getOrderNum()) // 分区键
                .setHeader(MessageConst.PROPERTY_TAGS, "order")
                .build();
        streamBridge.send(ORDER_BINDINGNAME, msg);
    }

    public void sendNormalMessage(Log log) {
        Message<Log> msg = MessageBuilder.withPayload(log).build();
        streamBridge.send(NORMAL_BINDINGNAME, msg);
    }

//    @Bean
//    public Supplier<Log> producer() {
//        return () -> {
//            Log log = new Log();
//            log.setLogName("producer");
//            return log;
//        };
//    }
}
