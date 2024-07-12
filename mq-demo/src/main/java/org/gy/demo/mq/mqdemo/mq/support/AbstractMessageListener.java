package org.gy.demo.mq.mqdemo.mq.support;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import java.nio.charset.StandardCharsets;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.gy.demo.mq.mqdemo.executor.EventMessageDispatchService;
import org.gy.demo.mq.mqdemo.model.EventStringMessage;
import org.gy.demo.mq.mqdemo.redis.RedisCacheKey;
import org.gy.framework.lock.core.DistributedLock;
import org.gy.framework.lock.core.support.RedisDistributedLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author gy
 */
@Slf4j
public abstract class AbstractMessageListener {

    @Value("${demoConfig.idempotent.expireTime:7200}")
    private int expireTime;

    @Value("${demoConfig.retryTimes:6}")
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

    protected void messageHandler(MessageExt msg) {
        String msgId = msg.getMsgId();
        String msgBody = new String(msg.getBody(), StandardCharsets.UTF_8);
        int retryTimes = getRetryTimes();
        if (retryTimes > 0 && msg.getReconsumeTimes() > retryTimes) {
            // 重试次数超过限制，不再处理，后续可以记录异常表或告警处理
            log.warn("[MessageListener]消息重试次数超过限制: msgId={},msgBody={},retryTimes={}", msgId, msgBody,
                retryTimes);
            return;
        }
        EventStringMessage eventMessage;
        try {
            eventMessage = JSON.parseObject(msgBody, EventStringMessage.class);
        } catch (Exception e) {
            log.warn("[MessageListener]消息解析异常: msgId={},msgBody={}.", msgId, msgBody, e);
            return;
        }
        if (eventMessage == null || eventMessage.getEventType() == null) {
            log.warn("[MessageListener]消息参数错误: msgId={},msgBody={}", msgId, msgBody);
            return;
        }
        log.info("[MessageListener]消息数据: msgId={},msgBody={}", msgId, msgBody);
//        String bizKey = event.getBizKey();
//        // 如果业务key为空，不做幂等校验
//        if (StrUtil.isBlank(bizKey)) {
//            eventMessageDispatchService.execute(event);
//            return;
//        }
        //如果没有传bizKey，则已msgId作为幂等key
        String idempotentKey = StrUtil.isNotBlank(eventMessage.getBizKey()) ? eventMessage.getBizKey() : msgId;
        String code = String.valueOf(eventMessage.getEventType().getCode());
        String redisKey = RedisCacheKey.getKey(RedisCacheKey.CACHE_ROCKETMQ_IDEMPOTENT_KEY, code, idempotentKey);
        DistributedLock lock = new RedisDistributedLock(stringRedisTemplate, redisKey, getExpireTime());
        boolean lockFlag = lock.tryLock();
        if (!lockFlag) {
            log.warn("[MessageListener]消息已经处理: redisKey={},msgId={},msgBody={}", redisKey, msgId, msgBody);
            return;
        }
        try {
            eventMessageDispatchService.execute(eventMessage);
        } catch (Throwable e) {
            //释放幂等key，抛出原异常，方便下次重试处理
            lock.unlock();
            throw e;
        }
    }

}
