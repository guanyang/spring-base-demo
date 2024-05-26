package org.gy.demo.redisdemo.handler.lock;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 */
public interface DistributedLockCallback<T> {

    T run();
}
