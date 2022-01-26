package org.gy.demo.mq.mqdemo;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import lombok.extern.slf4j.Slf4j;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2021/8/13 00:53
 */
@Slf4j
public class ProducerTest {

    private static final String nameServer = "127.0.0.1:9876";
    private static final String topic = "PushTopic_tt1";
    private static final int retryTimesWhenSendFailed = 3;


    public static void main(String[] args) {

//        producerSend(producer -> {
//            for (int i = 0; i < 10; i++) {
//                Message msg = new Message(topic, "TagA", "OrderID0034", ("message" + i).getBytes());
//                SendResult send = producer.send(msg);
//                log.info("发送消息：id={},result={}", send.getMsgId(), send.getSendStatus());
//            }
//        });

        producerSend(producer -> {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 3; j++) {
                    Message msg = new Message(topic, "TagA", "OrderID" + i + "-" + j,
                        ("message" + i + "-" + j).getBytes());
                    SendResult send = producer.send(msg, getMessageQueueSelector(), i);
                    log.info("发送消息：id={},result={}", send.getMsgId(), send.getSendStatus());
                }
            }
        });

    }

    private static MessageQueueSelector getMessageQueueSelector() {
        return (mqs, msg, arg) -> {
            try {
                int i = arg.hashCode();
                int index = Math.abs(i) % mqs.size();
                return mqs.get(index);
            } catch (Exception e) {
                return mqs.get(0);
            }
        };
    }


    private static void producerSend(SendExecute execute) {
        DefaultMQProducer producer = new DefaultMQProducer("quick_start");
        producer.setNamesrvAddr(nameServer);
        try {
            producer.setInstanceName("quick_start_producer");
            producer.setRetryTimesWhenSendFailed(retryTimesWhenSendFailed);
            producer.start();

            execute.send(producer);

        } catch (Exception e) {
            e.printStackTrace();
        }
        producer.shutdown();
    }

    public interface SendExecute {

        void send(DefaultMQProducer producer) throws Exception;
    }


}
