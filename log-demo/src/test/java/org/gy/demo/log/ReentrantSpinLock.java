package org.gy.demo.log;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 功能描述：简单自旋锁，可重入
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/8/25 19:19
 */
public class ReentrantSpinLock implements Lock {

    //利用CAS操作，解决多线程并发操作导致数据不一致的问题
    private AtomicReference<Thread> owner = new AtomicReference<>();
    //加入计数器，支持可重入
    private int count = 0;

    @Override
    public void lock() {
        Thread currentThread = Thread.currentThread();
        //如果锁被当前线程占用，则计数器+1
        if (currentThread == owner.get()) {
            count++;
            return;
        }
        // 如果锁未被占用，则设置当前线程为锁的拥有者
        while (!owner.compareAndSet(null, currentThread)) {
        }
    }

    @Override
    public void unlock() {
        Thread currentThread = Thread.currentThread();
        if (currentThread == owner.get()) {
            //count大于0，说明锁被占用多次，执行-1
            if (count > 0) {
                count--;
            } else {
                //执行锁释放
                owner.compareAndSet(currentThread, null);
            }
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException();
    }


    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }
}
