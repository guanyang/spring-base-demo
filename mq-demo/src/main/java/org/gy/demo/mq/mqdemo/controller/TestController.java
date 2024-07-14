package org.gy.demo.mq.mqdemo.controller;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2021/8/16 21:20
 */
@Slf4j
@RequestMapping("/test")
@RestController
public class TestController {

    @GetMapping("/lock")
    public Object lock(TestReq req, String id) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", req.getName());
        result.put("age", req.getAge());
        result.put("id", id);
        return result;
    }

//    @Resource(name = "demoProducer")
//    private RocketMqProducer demoProducer;
//
//    @GetMapping("/sendMsg")
//    public Response sendMsg(String msg, String tag) {
//        Message mQMsg = new Message(demoProducer.getRocketMQProperties().getTopic(), tag, msg.getBytes());
//        try {
//            SendResult sendResult = demoProducer.getProducer().send(mQMsg);
//            SendStatus sendStatus = sendResult.getSendStatus();
//            log.info("发送消息：{}", sendResult);
//            if (SendStatus.SEND_OK.equals(sendStatus)) {
//                return Response.asSuccess(sendResult);
//            } else {
//                return Response.asError(1001, sendStatus.name());
//            }
//        } catch (Exception e) {
//            log.error("sendMsg exception:msg={},tag={}.", msg, tag, e);
//            return Response.asError(1002, "消息发送失败");
//        }
//    }
//
//    @Value("${rocketmq.demo.topic}")
//    private String topic;
//
//    @Resource(name = "extRocketMQTemplate")
//    private ExtRocketMQTemplate extRocketMQTemplate;
//
//    @GetMapping("/sendMsg2")
//    public Response sendMsg2(String msg, String tag) {
//        try {
//            //一般是放topic，也可以放topic:tag
//            SendResult sendResult = extRocketMQTemplate.syncSend(topic + ":" + tag, msg);
//            SendStatus sendStatus = sendResult.getSendStatus();
//            log.info("发送消息：{}", sendResult);
//            if (SendStatus.SEND_OK.equals(sendStatus)) {
//                return Response.asSuccess(sendResult);
//            } else {
//                return Response.asError(1001, sendStatus.name());
//            }
//        } catch (Exception e) {
//            log.error("sendMsg exception:msg={},tag={}.", msg, tag, e);
//            return Response.asError(1002, "消息发送失败");
//        }
//    }

//    @Resource(name = "demoPulsarProducer")
//    private Producer<String> demoPulsarProducer;
//
//    @GetMapping("/sendMsg3")
//    public Response sendMsg3(String msg) {
//        try {
//            MessageId sendResult = demoPulsarProducer.send(msg);
//            log.info("发送消息：{}", sendResult);
//            return Response.asSuccess(sendResult);
//        } catch (Exception e) {
//            log.error("sendMsg exception:msg={}.", msg, e);
//            return Response.asError(1002, "消息发送失败");
//        }
//    }


    @Data
    public static class TestReq {

        private String name;

        private Integer age;
    }

}
