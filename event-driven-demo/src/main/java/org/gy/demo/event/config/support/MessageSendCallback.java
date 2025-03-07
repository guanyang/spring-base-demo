package org.gy.demo.event.config.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.gy.demo.event.model.EventLogContext;

@Slf4j
public class MessageSendCallback implements SendCallback {
    @Override
    public void onSuccess(SendResult sendResult) {
        log.info("[MessageSendCallback]发送成功：result={}", sendResult);
    }

    @Override
    public void onException(Throwable e) {
        //仅告警处理，后续考虑写入异常表，进行异步补偿处理
        log.error("[MessageSendCallback]发送失败", e);
    }
}
