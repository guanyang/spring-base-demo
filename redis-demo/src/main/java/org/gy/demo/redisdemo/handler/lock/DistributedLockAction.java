package org.gy.demo.redisdemo.handler.lock;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 */
public class DistributedLockAction {


    /**
     * 功能描述：业务执行，包含加锁、释放锁(非阻塞锁，仅尝试一次获取锁)
     *
     * @param lock 分布式锁定义
     * @param runnable 执行体
     */
    public static <T> LockResult<T> execute(DistributedLock lock, DistributedLockCallback<T> runnable) {
        return execute(lock, 0, 0, runnable);
    }

    /**
     * 功能描述:业务执行，包含加锁、释放锁(阻塞锁，一直阻塞)
     *
     * @param lock 分布式锁定义
     * @param sleepTimeMillis 睡眠重试时间，单位：毫秒
     * @param runnable 执行体
     */
    public static <T> LockResult<T> execute(DistributedLock lock, long sleepTimeMillis,
        DistributedLockCallback<T> runnable) {
        return execute(lock, -1, sleepTimeMillis, runnable);
    }

    /**
     * 功能描述:业务执行，包含加锁、释放锁(阻塞锁，自定义阻塞时间)
     *
     * @param lock 分布式锁定义
     * @param waitTimeMillis 等待超时时间，单位：毫秒
     * @param sleepTimeMillis 睡眠重试时间，单位：毫秒
     * @param runnable 执行体
     */
    public static <T> LockResult<T> execute(DistributedLock lock, long waitTimeMillis, long sleepTimeMillis,
        DistributedLockCallback<T> runnable) {
        boolean lockFlag = false;
        try {
            lockFlag = lock.tryLock(waitTimeMillis, sleepTimeMillis);
            if (!lockFlag) {
                return LockResult.wrapError();
            }
            T data = runnable.run();
            return LockResult.wrapSuccess(data);
        } finally {
            if (lockFlag) {
                lock.unlock();
            }
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LockResult<T> {

        public static final int SUCCESS = 0;

        public static final String SUCCESS_MSG = "操作成功";

        public static final int ERROR = -1;

        public static final String ERROR_MSG = "操作失败";

        private int error;
        private String msg;
        private T data;

        public boolean success() {
            return error == SUCCESS;
        }

        public static <T> LockResult<T> wrapSuccess(T data) {
            return wrapResult(SUCCESS, SUCCESS_MSG, data);
        }

        public static <T> LockResult<T> wrapError() {
            return wrapResult(ERROR, ERROR_MSG, null);
        }

        public static <T> LockResult<T> wrapResult(int error, String msg, T data) {
            return new LockResult<>(error, msg, data);
        }
    }

}
