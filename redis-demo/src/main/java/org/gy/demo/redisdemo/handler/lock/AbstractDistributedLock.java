package org.gy.demo.redisdemo.handler.lock;


import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 */
@Slf4j
public abstract class AbstractDistributedLock implements DistributedLock {

    public static final long SLEEP_TIME_MILLIS = 50;

    public static final long EXPIRE_TIME_MILLIS = 30000;
    /**
     * 锁key
     */
    private final String lockKey;
    /**
     * 锁的值，请求ID
     */
    private final String requestId;
    /**
     * 过期时间，单位：毫秒
     */
    private final long expireMillis;
    /**
     * 加锁是否成功，true是，false否
     */
    private volatile boolean locked = false;

    public AbstractDistributedLock(String lockKey) {
        this(lockKey, EXPIRE_TIME_MILLIS);
    }

    public AbstractDistributedLock(String lockKey, long expireMillis) {
        this(lockKey, defaultRequestId(), expireMillis);
    }

    public AbstractDistributedLock(String lockKey, String requestId, long expireMillis) {
        this.lockKey = lockKey;
        this.requestId = requestId;
        this.expireMillis = checkExpireTime(expireMillis);
    }

    private static String defaultRequestId() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    private static long checkExpireTime(long expireMillis) {
        return expireMillis > 0 ? expireMillis : EXPIRE_TIME_MILLIS;
    }

    private static long checkSleepTime(long sleepTimeMillis) {
        return sleepTimeMillis > 0 ? sleepTimeMillis : SLEEP_TIME_MILLIS;
    }

    @Override
    public boolean tryLock() {
        return tryLock(0, 0);
    }

    @Override
    public boolean tryLock(long waitTimeMillis, long sleepTimeMillis) {
        try {
            long startTime = System.currentTimeMillis();
            if (waitTimeMillis == 0) {
                //此情况，不需要等待重试
                locked = innerLock(lockKey, requestId, expireMillis);
                return locked;
            }
            long sleepTime = checkSleepTime(sleepTimeMillis);
            while (checkCondition(startTime, waitTimeMillis)) {
                locked = innerLock(lockKey, requestId, expireMillis);
                if (locked) {
                    return true;
                }
                // 针对有超时的场景，如果此时耗费时间加上sleepTime已经超时，直接返回失败即可，不需要sleep，可提升效率
                if (waitTimeMillis > 0) {
                    long time = (System.currentTimeMillis() - startTime) + sleepTime - waitTimeMillis;
                    if (time >= 0) {
                        return false;
                    }
                }
                TimeUnit.MILLISECONDS.sleep(sleepTime);
            }
        } catch (Exception e) {
            log.warn("[DistributedLock]加锁异常:lockKey={},expireMillis={},waitTimeMillis={},sleepTimeMillis={}.",
                lockKey, expireMillis, waitTimeMillis, sleepTimeMillis, e);
        }
        return false;
    }

    @Override
    public void lock(long sleepTimeMillis) {
        tryLock(-1, sleepTimeMillis);
    }

    @Override
    public boolean unlock() {
        if (locked) {
            boolean unLock = innerUnLock(lockKey, requestId);
            if (unLock) {
                locked = false;
            }
            return unLock;
        }
        return false;
    }

    private boolean checkCondition(long startTime, long waitTimeMillis) {
        if (waitTimeMillis < 0) {
            return true;
        }
        return (System.currentTimeMillis() - startTime) < waitTimeMillis;
    }


    /**
     * 功能描述：获取锁
     *
     * @param lockKey 锁key
     * @param requestId 锁的值，请求ID
     * @param expireMillis 过期时间，单位：毫秒
     * @return 获取锁是否成功，true是，false否
     * @author gy
     */
    public abstract boolean innerLock(String lockKey, String requestId, long expireMillis);

    /**
     * 功能描述：释放锁
     *
     * @param lockKey 锁key
     * @param requestId 锁的值，请求ID
     * @return 释放锁是否成功，true是，false否
     * @author gy
     */
    public abstract boolean innerUnLock(String lockKey, String requestId);

}
