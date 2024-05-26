package org.gy.demo.redisdemo.handler.lock;

/**
 * 功能描述：分布式锁定义
 *
 * @author gy
 * @version 1.0.0
 */
public interface DistributedLock {

    /**
     * 功能描述: 非阻塞锁，仅尝试一次获取锁
     *
     * @return 加锁是否成功，true成功，false不成功
     */
    boolean tryLock();

    /**
     * 功能描述: 阻塞锁，自定义阻塞时间
     *
     * @param waitTimeMillis 等待超时时间，单位：毫秒
     * @param sleepTimeMillis 睡眠重试时间，单位：毫秒
     * @return 加锁是否成功，true成功，false不成功
     */
    boolean tryLock(long waitTimeMillis, long sleepTimeMillis);

    /**
     * 功能描述: 阻塞锁，一直阻塞
     *
     * @param sleepTimeMillis 睡眠重试时间，单位：毫秒
     */
    void lock(long sleepTimeMillis);

    /**
     * 功能描述：释放锁
     *
     * @return 释放锁是否成功，true成功，false不成功
     */
    boolean unlock();

}
