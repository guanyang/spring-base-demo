package org.gy.demo.mq.mqdemo.mq.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageExt;
import org.gy.demo.mq.mqdemo.executor.EventMessageDispatchService;
import org.gy.demo.mq.mqdemo.executor.EventMessageService;
import org.gy.demo.mq.mqdemo.model.EventLogContext;
import org.gy.demo.mq.mqdemo.model.EventMessage;
import org.gy.demo.mq.mqdemo.model.EventMessageDispatchResult;
import org.gy.demo.mq.mqdemo.redis.RedisCacheKey;
import org.gy.framework.core.util.JsonUtils;
import org.gy.framework.lock.core.DistributedLock;
import org.gy.framework.lock.core.support.RedisDistributedLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Optional;

/**
 * @author gy
 */
@Slf4j
public abstract class AbstractMessageListener {

    @Value("${demoConfig.idempotent.expireTime:7200}")
    private int expireTime;

    @Value("${demoConfig.retryTimes:1}")
    private int retryTimes;

    @Resource
    private EventMessageDispatchService eventMessageDispatchService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    protected int getRetryTimes() {
        return retryTimes;
    }

    protected int getExpireTime() {
        return expireTime;
    }

    protected void messageHandler(MessageExt msg) throws Throwable {
        String msgId = msg.getMsgId();
        String msgBody = new String(msg.getBody(), StandardCharsets.UTF_8);
        EventMessage<?> eventMessage = JsonUtils.toObject(msgBody, EventMessage.class);
        if (eventMessage == null || eventMessage.getEventType() == null) {
            log.warn("[MessageListener]消息参数错误: msgId={},msgBody={}", msgId, msgBody);
            return;
        }
        log.info("[MessageListener]消息数据: msgId={},msgBody={}", msgId, msgBody);

        String idempotentKey = getUniqueKey(eventMessage);
        String code = String.valueOf(eventMessage.getEventType().getCode());
        String redisKey = RedisCacheKey.getKey(RedisCacheKey.CACHE_ROCKETMQ_IDEMPOTENT_KEY, code, idempotentKey);
        DistributedLock lock = new RedisDistributedLock(stringRedisTemplate, redisKey, getExpireTime());
        boolean lockFlag = lock.tryLock();
        if (!lockFlag) {
            log.warn("[MessageListener]消息已经处理: redisKey={},msgId={},msgBody={}", redisKey, msgId, msgBody);
            return;
        }
        internalExecute(eventMessage, msg, lock);
    }

    private void internalExecute(EventMessage<?> eventMessage, MessageExt msg, DistributedLock lock) throws Throwable {
        String msgId = msg.getMsgId();
        EventLogContext<EventMessage<?>, Object> ctx = EventLogContext.builder().requestId(eventMessage.getRequestId()).request(eventMessage).build();
        EventMessageDispatchResult dispatchResult = eventMessageDispatchService.execute(eventMessage);
        if (dispatchResult.hasException()) {
            //释放幂等key，抛出原异常，方便下次重试处理
            lock.unlock();
            //异常重试处理
            internalException(dispatchResult, msg);
            ctx.setEx(dispatchResult.getEx());
        } else {
            ctx.setResponse(dispatchResult.getResult());
        }
        // 保存事件日志（异步）
        EventLogContext.handleEventLog(Collections.singletonList(ctx));
    }

    private void internalException(EventMessageDispatchResult dispatchResult, MessageExt msg) throws Throwable {
        Throwable ex = dispatchResult.getEx();
        EventMessageService eventMessageService = dispatchResult.getService();
        boolean supportRetry = Optional.ofNullable(eventMessageService).map(s -> s.supportRetry(ex)).orElse(false);

        String msgId = msg.getMsgId();
        String msgBody = new String(msg.getBody(), StandardCharsets.UTF_8);
        if (!supportRetry) {
            log.warn("[MessageListener]业务异常暂不支持重试: msgId={},msgBody={}", msgId, msgBody, ex);
            return;
        }
        int retryTimes = getRetryTimes();
        if (retryTimes > 0 && msg.getReconsumeTimes() > retryTimes) {
            // 重试次数超过限制，不再处理，后续可以记录异常表或告警处理
            log.warn("[MessageListener]消息重试次数超过限制: msgId={},msgBody={},retryTimes={}", msgId, msgBody, retryTimes, ex);
        } else {
            throw ex;
        }
    }


    private String getUniqueKey(EventMessage<?> eventMessage) {
        //如果没有传bizKey，则已requestId作为幂等key
        return Optional.ofNullable(eventMessage.getBizKey()).filter(StringUtils::isNotBlank).orElseGet(eventMessage::getRequestId);
    }

}
