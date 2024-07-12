package org.gy.demo.mq.mqdemo;

import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.gy.demo.mq.mqdemo.executor.EventMessageSendService;
import org.gy.demo.mq.mqdemo.model.EventMessage;
import org.gy.demo.mq.mqdemo.model.EventType;
import org.gy.demo.mq.mqdemo.trace.TraceContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = MqDemoApplication.class)
public class EventMessageSendServiceTest {

    @Resource
    private EventMessageSendService eventMessageSendService;

    // 在每个测试方法之前运行
    @BeforeEach
    public void setUp() {
        TraceContext.getTrace();
    }

    // 在每个测试方法之后运行
    @AfterEach
    public void tearDown() {
        TraceContext.clearTrace();
    }


    @Test
    public void demoMsg() {
        for (int i = 0; i < 3; i++) {
            String msg = "msg" + i;
//            String bizKey = "bizKey" + i;
//            EventMessage<String> event = EventMessage.of(EventType.DEMO_EVENT, msg, bizKey);
            EventMessage<String> event = EventMessage.of(EventType.DEMO_EVENT, msg);
            log.info("demoMsg发送消息开始：req={}", event);
            SendResult sendResult = eventMessageSendService.sendNormalMessage(event);
            log.info("demoMsg发送消息结束：res={}", sendResult);
        }
    }

    @Test
    public void orderlyMsg() {
        for (int i = 0; i < 3; i++) {
            String msg = "msg" + i;
//            String bizKey = "bizKey" + i;
//            EventMessage<String> event = EventMessage.of(EventType.DEMO_EVENT, msg, bizKey);
            EventMessage<String> event = EventMessage.of(EventType.DEMO_EVENT, msg);
            event.setOrderlyKey("orderlyMsg");
            log.info("orderlyMsg发送消息开始：req={}", event);
            SendResult sendResult = eventMessageSendService.sendOrderlyMessage(event);
            log.info("orderlyMsg发送消息结束：res={}", sendResult);
        }
    }

}
