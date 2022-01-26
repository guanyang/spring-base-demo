package org.gy.demo.mq.mqdemo.config;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.common.message.MessageExt;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2021/8/13 00:17
 */
@Component
@Slf4j
public class MQConsumeMsgListenerProcessor implements MessageListenerConcurrently {

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
        ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        for (MessageExt msg : msgs) {
            try {
                String topic = msg.getTopic();
                String msgBody = new String(msg.getBody(), "utf-8");
                String tags = msg.getTags();
                System.out.println("收到消息:topic:" + topic + ",tags:" + tags + ",msgBody:" + msgBody);
                if ("message3".equals(msgBody)) {
                    System.out.println("====失败消息开始=====");
                    System.out.println("msg:" + msg);
                    System.out.println("msgBody:" + msgBody);
                    System.out.println("====失败消息结束=====");
                    // 发生异常
                    int i = 1 / 0;
                    System.out.println(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (msg.getReconsumeTimes() == 3) {
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
