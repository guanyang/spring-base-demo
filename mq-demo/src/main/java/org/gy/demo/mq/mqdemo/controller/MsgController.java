package org.gy.demo.mq.mqdemo.controller;


import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.shade.io.swagger.annotations.ApiOperation;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.gy.demo.mq.mqdemo.executor.DemoEventMessageService;
import org.gy.demo.mq.mqdemo.executor.EventMessageSendService;
import org.gy.demo.mq.mqdemo.executor.EventMessageService;
import org.gy.demo.mq.mqdemo.executor.support.EventMessageServiceManager;
import org.gy.demo.mq.mqdemo.model.EventMessage;
import org.gy.demo.mq.mqdemo.model.EventType;
import org.gy.framework.core.dto.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author gy
 */
@Slf4j
@RequestMapping("/msg")
@RestController
public class MsgController {

    @Resource
    private EventMessageSendService eventMessageSendService;

    @Resource
    private DemoEventMessageService demoEventMessageService;

    //普通消息测试
    @GetMapping("/demoMsg")
    public Response<Void> demoMsg(int total) {
        List<EventMessage<String>> list = Lists.newArrayList();
        for (int i = 0; i < total; i++) {
            String msg = "msg" + i;
            EventMessage<String> event = EventMessage.of(EventType.DEMO_EVENT, msg);
            list.add(event);
        }
        log.info("demoMsg发送消息开始：req={}", list);
        eventMessageSendService.sendNormalMessageAsync(list);
        log.info("demoMsg发送消息结束");
        return Response.asSuccess();
    }

    //延迟消息测试
    @GetMapping("/delayMsg")
    public Response<Void> delayMsg(int total) {
        for (int i = 0; i < total; i++) {
            String msg = "msg" + i;
            EventMessage<String> event = EventMessage.of(EventType.DEMO_EVENT, msg);
            event.setDelayTimeLevel(2);
            log.info("delayMsg发送消息开始：req={}", event);
            SendResult sendResult = eventMessageSendService.sendNormalMessage(event);
            log.info("delayMsg发送消息结束：res={}", sendResult);
        }
        return Response.asSuccess();
    }

    //顺序消息测试
    @GetMapping("/orderlyMsg")
    public Response<Void> orderlyMsg(int total) {
        for (int i = 0; i < total; i++) {
            String msg = "msg" + i;
            EventMessage<String> event = EventMessage.of(EventType.DEMO_EVENT, msg);
            event.setOrderlyKey("orderlyMsg");
            log.info("orderlyMsg发送消息开始：req={}", event);
            SendResult sendResult = eventMessageSendService.sendOrderlyMessage(event);
            log.info("orderlyMsg发送消息结束：res={}", sendResult);
        }
        return Response.asSuccess();
    }

    //事务消息测试
    @GetMapping("/transactionMsg")
    public Response<Void> transactionMsg(int total) {
        for (int i = 0; i < total; i++) {
            String msg = "msg" + i;
            EventMessage<String> event = EventMessage.of(EventType.DEMO_EVENT, msg);
            log.info("transactionMsg发送消息开始：req={}", event);
            TransactionSendResult sendResult = eventMessageSendService.sendTransactionMessage(event);
            log.info("transactionMsg发送消息结束：res={}", sendResult);
        }
        return Response.asSuccess();
    }

    @ApiOperation("动态事件测试")
    @GetMapping("/dynamicEvent")
    public Response<Void> dynamicEvent(String data, Integer handleType) {
        EventType eventType = EventType.DYNAMIC_DEMO_EVENT;
        EventMessage<String> req = EventMessage.of(eventType, data);
        log.info("[dynamicEvent]发送消息开始：{}", req);
        Optional<EventMessageService<String, Object>> service = EventMessageServiceManager.getService(eventType);
        if (handleType == null || handleType == 1) {
            demoEventMessageService.dynamicEventTest(data);
        } else if (handleType == 2) {
            service.ifPresent(s -> s.asyncSend(req));
        } else if (handleType == 3) {
            service.ifPresent(s -> s.synchronousSend(req));
        }
        log.info("[dynamicEvent]发送消息结束");
        return Response.asSuccess();
    }

}
