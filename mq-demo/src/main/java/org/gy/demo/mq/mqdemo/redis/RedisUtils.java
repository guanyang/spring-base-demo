package org.gy.demo.mq.mqdemo.redis;

import org.gy.framework.lock.core.DistributedLock;
import org.gy.framework.lock.core.DistributedLockAction;
import org.gy.framework.lock.core.DistributedLockCallback;
import org.gy.framework.lock.core.support.RedisDistributedLock;
import org.gy.framework.lock.exception.DistributedLockException;
import org.gy.framework.lock.model.LockResult;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * redis工具类
 *
 * @author gy
 * @version 1.0.0
 */
public class RedisUtils {

    private RedisUtils() {
    }

    /**
     * 业务并发执行，包含加锁、释放锁(非阻塞锁，仅尝试一次获取锁)
     *
     * @param redisTemplate Redis模板，用于执行Redis操作。
     * @param runnable 分布式锁执行的回调接口，其中包含需要加锁执行的逻辑。
     * @param cacheKeyEnum 缓存键类型枚举，用于生成Redis键。
     * @param params 用于生成Redis键的参数。
     * @param <T> 回调方法的返回类型。
     * @return 返回方法结果值。
     */
    public static <T> T execute(StringRedisTemplate redisTemplate, DistributedLockCallback<T> runnable,
        RedisCacheKey cacheKeyEnum, String... params) {
        return execute(redisTemplate, 0, 0, runnable, cacheKeyEnum, params);
    }

    /**
     * 业务并发执行，包含加锁、释放锁(阻塞锁，一直阻塞直到成功)
     *
     * @param redisTemplate Redis模板，用于执行Redis操作。
     * @param sleepTimeMillis 等待锁的间隔时间（毫秒）。
     * @param runnable 分布式锁执行的回调接口，其中包含需要加锁执行的逻辑。
     * @param cacheKeyEnum 缓存键类型枚举，用于生成Redis键。
     * @param params 用于生成Redis键的参数。
     * @param <T> 回调方法的返回类型。
     * @return 返回方法结果值。
     */
    public static <T> T execute(StringRedisTemplate redisTemplate, long sleepTimeMillis,
        DistributedLockCallback<T> runnable, RedisCacheKey cacheKeyEnum, String... params) {
        return execute(redisTemplate, -1, sleepTimeMillis, runnable, cacheKeyEnum, params);
    }

    /**
     * 业务并发执行，包含加锁、释放锁(阻塞锁，自定义阻塞时间)
     *
     * @param redisTemplate Redis模板，用于执行Redis操作。
     * @param waitTimeMillis 获取锁的最大等待时间（毫秒）。
     * @param sleepTimeMillis 等待锁的间隔时间（毫秒）。
     * @param runnable 分布式锁执行的回调接口，其中包含需要加锁执行的逻辑。
     * @param cacheKeyEnum 缓存键类型枚举，用于生成Redis键。
     * @param params 用于生成Redis键的参数。
     * @param <T> 回调方法的返回类型。
     * @return 返回方法结果值。
     */
    public static <T> T execute(StringRedisTemplate redisTemplate, long waitTimeMillis, long sleepTimeMillis,
        DistributedLockCallback<T> runnable, RedisCacheKey cacheKeyEnum, String... params) {
        LockResult<T> lockResult = lockExecute(redisTemplate, waitTimeMillis, sleepTimeMillis, runnable, cacheKeyEnum,
            params);
        if (!lockResult.success()) {
            throw new DistributedLockException(lockResult.getError(), lockResult.getMsg());
        }
        return lockResult.getData();
    }

    /**
     * 业务并发执行，包含加锁、释放锁(阻塞锁，自定义阻塞时间)
     *
     * @param redisTemplate Redis模板，用于执行Redis操作。
     * @param waitTimeMillis 获取锁的最大等待时间（毫秒）。
     * @param sleepTimeMillis 等待锁的间隔时间（毫秒）。
     * @param runnable 分布式锁执行的回调接口，其中包含需要加锁执行的逻辑。
     * @param cacheKeyEnum 缓存键类型枚举，用于生成Redis键。
     * @param params 用于生成Redis键的参数。
     * @param <T> 回调方法的返回类型。
     * @return 返回分布式锁执行的结果，包含执行成功与否和结果值。
     */
    public static <T> LockResult<T> lockExecute(StringRedisTemplate redisTemplate, long waitTimeMillis,
        long sleepTimeMillis, DistributedLockCallback<T> runnable, RedisCacheKey cacheKeyEnum, String... params) {
        // 生成基于参数和缓存键类型枚举的Redis键
        String redisKey = RedisCacheKey.getKey(cacheKeyEnum, params);
        // 使用Redis模板和生成的Redis键创建分布式锁实例
        DistributedLock lock = new RedisDistributedLock(redisTemplate, redisKey,
            (int) (cacheKeyEnum.getExpireMillis() / 1000));
        // 尝试加锁并执行回调操作，返回执行结果
        return DistributedLockAction.execute(lock, waitTimeMillis, sleepTimeMillis, runnable);
    }


}
