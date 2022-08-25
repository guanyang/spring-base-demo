package org.gy.demo.log;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/7/25 10:33
 */
public class FIFOMutex {

    private final AtomicBoolean locked = new AtomicBoolean(false);

    private final Queue<Thread> waiters = new ConcurrentLinkedQueue<Thread>();

    public void lock() {
        boolean wasInterrupted = false;
        Thread current = Thread.currentThread();
        waiters.add(current);

        // 如果当前线程不是等待线程队列第一个，或者locked状态已经是true，那么当前线程就要等待
        while (waiters.peek() != current || !locked.compareAndSet(false, true)) {
            System.out.println(Thread.currentThread().getName() + "  park start");
            LockSupport.park(this);
            System.out.println(Thread.currentThread().getName() + "  park end");
            // 等待线程的中断线程标志位为true，就设置wasInterrupted为true
            if (Thread.interrupted()) {
                wasInterrupted = true;
            }
        }

        // 移除第一个元素。当前线程就是第一个元素，因为while判断条件
        waiters.remove();
        // 如果wasInterrupted为true，当前线程发出中断请求
        if (wasInterrupted) {
            current.interrupt();
        }
        System.out.println(Thread.currentThread().getName() + " lock end");
    }

    // 唤醒可能等待的线程
    public void unlock() {
        System.out.println(Thread.currentThread().getName() + "  unpark start ");
        // 将locked设置为false
        locked.set(false);
        // 唤醒当前线程队列中第一个元素
        LockSupport.unpark(waiters.peek());
        System.out.println(Thread.currentThread().getName() + "  unpark end ");
    }


}
