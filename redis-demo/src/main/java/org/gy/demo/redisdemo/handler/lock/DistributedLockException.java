package org.gy.demo.redisdemo.handler.lock;

/**
 * 分布式锁异常类
 *
 * @author gy
 * @version 1.0.0
 */
public class DistributedLockException extends RuntimeException {

    private static final long serialVersionUID = 303295882885179551L;


    public DistributedLockException() {
        super();
    }

    public DistributedLockException(String message) {
        super(message);
    }

}
