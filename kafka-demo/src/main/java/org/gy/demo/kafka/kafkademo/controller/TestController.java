package org.gy.demo.kafka.kafkademo.controller;

import lombok.extern.slf4j.Slf4j;
import org.gy.framework.core.dto.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author guanyang
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Resource
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic}")
    private String topic;

    @GetMapping("/send")
    public Response<Void> sendMessage(String msg) {
        log.info("发送消息：{}", msg);
        kafkaTemplate.send(topic, msg);
//         kafkaTemplate.send(topic, 1, "2" , msg);
        return Response.asSuccess();
    }

}
