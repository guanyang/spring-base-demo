package org.gy.demo.mq.mqdemo.mq;

import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.gy.demo.mq.mqdemo.executor.EventMessageDispatchService;
import org.gy.demo.mq.mqdemo.mq.support.AbstractMessageListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


/**
 * @author gy
 */
@Slf4j
@Component("demoListener")
public class DemoListener extends AbstractMessageListener implements MessageListenerConcurrently {

    @Value("${demoConfig.idempotent.expireTime:7200}")
    private int expireTime;

    @Resource
    private EventMessageDispatchService eventMessageDispatchService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        for (MessageExt msg : msgs) {
            long startTime = System.currentTimeMillis();
            String msgId = msg.getMsgId();
            try {
                messageHandler(msg);
            } catch (Throwable e) {
                log.error("[DemoListener]消费处理异常: msg={}.", msg, e);
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
            log.info("[DemoListener]消费处理成功：msgId={},time={}ms", msgId, (System.currentTimeMillis() - startTime));
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}
