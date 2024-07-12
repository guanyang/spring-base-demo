package org.gy.demo.mq.mqdemo.mq;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.message.MessageExt;
import org.gy.demo.mq.mqdemo.mq.support.AbstractMessageListener;
import org.springframework.stereotype.Component;

/**
 * @author gy
 */
@Slf4j
@Component("orderlyListener")
public class OrderlyMessageListener extends AbstractMessageListener implements MessageListenerOrderly {

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        for (MessageExt msg : msgs) {
            long startTime = System.currentTimeMillis();
            String msgId = msg.getMsgId();
            try {
                messageHandler(msg);
            } catch (Throwable e) {
                log.error("[OrderlyMessageListener]消费处理异常: msg={}.", msg, e);
                return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
            }
            log.info("[OrderlyMessageListener]消费处理成功：msgId={},time={}ms", msgId, (System.currentTimeMillis() - startTime));
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }
}
