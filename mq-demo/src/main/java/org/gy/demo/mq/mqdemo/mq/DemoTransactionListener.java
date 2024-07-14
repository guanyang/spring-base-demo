package org.gy.demo.mq.mqdemo.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

/**
 * @author gy
 */
@Slf4j
@Component("demoTransactionListener")
public class DemoTransactionListener implements TransactionListener {

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        // 本地事务逻辑
        log.info("[demoTransactionListener]executeLocal msg: {}, arg: {}", new String(msg.getBody()), arg);
        // 假设本地事务成功，可以返回 COMMIT_MESSAGE
        return LocalTransactionState.COMMIT_MESSAGE;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        // 检查本地事务状态
        log.info("[demoTransactionListener]checkLocal msg: {}", new String(msg.getBody()));
        // 假设本地事务检查通过，可以返回 COMMIT_MESSAGE
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
