package org.gy.demo.event.controller;

import org.gy.demo.event.model.Log;
import org.gy.demo.event.model.Order;
import org.gy.demo.event.service.MessageProducer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private MessageProducer messageProducer;

    @GetMapping("/hello")
    public String hello(String key) {
        return "Hello, " + key;
    }

    @GetMapping("/order")
    public void order(Order order) {
        messageProducer.sendOrderMessage(order);
    }

    @GetMapping("/log")
    public void log(Log log) {
        messageProducer.sendNormalMessage(log);
    }
}
