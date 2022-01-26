package org.gy.demo.mq.mqdemo;

import com.alibaba.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2021/8/25 13:38
 */
@Slf4j
public class ConsumerQueueAllocateTest {

    public static void main(String[] args) {
        List<String> cidAll = Lists.newArrayList("client1", "client2", "client3", "client4");
        List<MessageQueue> mqAll = Lists.newArrayList();
        for (int i = 0; i < 3; i++) {
            MessageQueue queue = new MessageQueue("topicName", "brokerName", i);
            mqAll.add(queue);
        }
        AllocateMessageQueueAveragely queueAveragely = new AllocateMessageQueueAveragely();
        cidAll.forEach(cid -> {
            List<MessageQueue> allocate = queueAveragely.allocate("group", cid, mqAll, cidAll);
            List<Integer> queueIds = allocate.stream().map(MessageQueue::getQueueId).collect(Collectors.toList());
            log.info("cid={},queueIds={}", cid, queueIds);
        });

    }

}
