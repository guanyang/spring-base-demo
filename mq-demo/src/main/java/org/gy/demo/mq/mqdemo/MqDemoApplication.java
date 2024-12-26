package org.gy.demo.mq.mqdemo;

import org.gy.demo.mq.mqdemo.executor.support.EnableDynamicEventStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author guanyang
 */
@SpringBootApplication
@EnableDynamicEventStrategy
public class MqDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqDemoApplication.class, args);
    }

}
