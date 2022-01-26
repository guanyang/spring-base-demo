package org.gy.demo.mq.mqdemo;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.MessageExt;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2021/8/13 00:53
 */
@Slf4j
public class ConsumerTest {

    private static final String nameServer = "127.0.0.1:9876";
    private static final String topic = "PushTopic_tt1";
    private static final String tags = "*";
    private static final int consumeThreadMin = 10;
    private static final int consumeThreadMax = 20;
    private static final int consumeMessageBatchMaxSize = 1;

    public static void main(String[] args) throws MQClientException {

//        consumerHandler(getMessageListenerConcurrently());

        consumerOrderHandler(getMessageListenerOrderly());

    }

    private static void msgHandler(MessageExt msg) throws Exception {
        String topic = msg.getTopic();
        String msgBody = new String(msg.getBody(), "utf-8");
        String tags = msg.getTags();
        int queueId = msg.getQueueId();
        log.info("收到消息:topic={},msgId={},msgBody={},tags={},queueId={}", topic, msg.getMsgId(), msgBody,
            tags, queueId);
        //模拟业务处理消息的时间
        Thread.sleep(ThreadLocalRandom.current().nextInt(1000));
    }

    public static MessageListenerConcurrently getMessageListenerConcurrently() {
        return (msgs, context) -> {
            for (MessageExt msg : msgs) {
                try {
                    msgHandler(msg);
                } catch (Exception e) {
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        };
    }

    public static MessageListenerOrderly getMessageListenerOrderly() {
        return (msgs, context) -> {
            for (MessageExt msg : msgs) {
                try {
                    msgHandler(msg);
                } catch (Exception e) {
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
            }
            return ConsumeOrderlyStatus.SUCCESS;
        };
    }

    public static void consumerOrderHandler(MessageListenerOrderly listener) throws MQClientException {
        DefaultMQPushConsumer consumer = getDefaultMQPushConsumer();
        consumer.registerMessageListener(listener);
        consumer.start();
        System.out.println("Consumer Started.");
    }


    public static void consumerHandler(MessageListenerConcurrently listener) throws MQClientException {
        DefaultMQPushConsumer consumer = getDefaultMQPushConsumer();
        consumer.registerMessageListener(listener);
        consumer.start();
        System.out.println("Consumer Started.");
    }

    private static DefaultMQPushConsumer getDefaultMQPushConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("quick_start");
        consumer.setNamesrvAddr(nameServer);
        // 批量消费,每次拉取10条
        consumer.setConsumeMessageBatchMaxSize(consumeMessageBatchMaxSize);//
        consumer.setConsumeThreadMin(consumeThreadMin);
        consumer.setConsumeThreadMax(consumeThreadMax);

        // 程序第一次启动从消息队列头取数据
        // 如果非第一次启动，那么按照上次消费的位置继续消费
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 订阅PushTopic下Tag为push的消息
        consumer.subscribe(topic, tags);
//        consumer.setInstanceName(UUID.randomUUID().toString());
        return consumer;
    }

}
