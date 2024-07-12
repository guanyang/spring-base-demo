package org.gy.demo.mq.mqdemo.mq;

import org.apache.rocketmq.client.hook.ConsumeMessageContext;
import org.apache.rocketmq.client.hook.ConsumeMessageHook;
import org.apache.rocketmq.common.message.MessageExt;
import org.gy.demo.mq.mqdemo.trace.TraceContext;
import org.gy.demo.mq.mqdemo.trace.TraceEnum;

/**
 * @author gy
 */
public class TraceConsumeMessageHook implements ConsumeMessageHook {

    private static final String DEFAULT_HOOK_NAME = "TraceConsumeMessageHook";

    @Override
    public String hookName() {
        return DEFAULT_HOOK_NAME;
    }

    @Override
    public void consumeMessageBefore(ConsumeMessageContext context) {
        if (context == null || context.getMsgList() == null || context.getMsgList().isEmpty()) {
            return;
        }
        //默认获取第一个消息的trace信息即可
        MessageExt messageExt = context.getMsgList().get(0);
        if (messageExt != null) {
            String traceId = messageExt.getUserProperty(TraceEnum.TRACE.getName());
            TraceContext.setTrace(traceId);
        }

    }

    @Override
    public void consumeMessageAfter(ConsumeMessageContext context) {
        TraceContext.clearTrace();
    }
}
