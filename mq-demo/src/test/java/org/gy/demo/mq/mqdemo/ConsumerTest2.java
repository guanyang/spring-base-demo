package org.gy.demo.mq.mqdemo;

import static org.gy.demo.mq.mqdemo.ConsumerTest.consumerOrderHandler;
import static org.gy.demo.mq.mqdemo.ConsumerTest.getMessageListenerOrderly;

import com.alibaba.rocketmq.client.exception.MQClientException;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2021/8/25 21:30
 */
public class ConsumerTest2 {

    public static void main(String[] args) throws MQClientException {

//        consumerHandler(getMessageListenerConcurrently());

        consumerOrderHandler(getMessageListenerOrderly());

    }

}
