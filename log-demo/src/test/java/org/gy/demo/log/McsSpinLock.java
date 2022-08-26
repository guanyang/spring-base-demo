package org.gy.demo.log;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 功能描述：MCS自旋公平锁，独占，不可重入
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/8/25 17:20
 */
public class McsSpinLock implements Lock {

    private final ThreadLocal<Node> current;
    private final AtomicReference<Node> tail;

    public McsSpinLock() {
        this.current = ThreadLocal.withInitial(Node::new);
        this.tail = new AtomicReference<>();
    }

    @Override
    public void lock() {
        Node node = current.get();
        Node pred = tail.getAndSet(node);
        // pred的初始值为null，所以第一个加锁线程，直接跳过判断，加锁成功
        if (pred != null) {
            pred.next = node;
            // 在当前节点的locked字段上自旋等待
            while (node.locked) {
            }
        }
    }


    @Override
    public void unlock() {
        Node node = current.get();
        if (node.next == null) {
            // 如果设置成功，说明在此之前没有线程进行lock操作，直接return即可；
            // 如果失败，则说明在此之前有线程进行lock操作，需要自旋等待那个线程将自身节点设置为本线程节点的next，
            // 然后进行后面的操作。
            if (tail.compareAndSet(node, null)) {
                return;
            }
            while (node.next == null) {
            }
        }
        // 通知下一个线程，使下一个线程加锁成功
        node.next.locked = false;
        // 解锁后需要将节点之间的关联断开，否则会产生内存泄露ø
        node.next = null;
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

    static class Node {

        volatile boolean locked = true;
        volatile Node next;
    }
}
