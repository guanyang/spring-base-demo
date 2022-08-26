package org.gy.demo.log;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 功能描述：CLH自旋公平锁，独占，不可重入
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/8/19 17:34
 */
public class ClhSpinLock implements Lock {

    private final ThreadLocal<Node> prev;
    private final ThreadLocal<Node> node;
    private final AtomicReference<Node> tail;

    public ClhSpinLock() {
        this.node = ThreadLocal.withInitial(Node::new);
        this.prev = ThreadLocal.withInitial(() -> null);
        this.tail = new AtomicReference<Node>(new Node());
    }

    /**
     * 1.初始状态 tail指向一个node(head)节点
     * +------+
     * | head | <---- tail
     * +------+
     *
     * 2.lock-thread加入等待队列: tail指向新的Node，同时Prev指向tail之前指向的节点
     * +----------+
     * | Thread-A |
     * | := Node  | <---- tail
     * | := Prev  | -----> +------+
     * +----------+        | head |
     *                     +------+
     *
     *             +----------+            +----------+
     *             | Thread-B |            | Thread-A |
     * tail ---->  | := Node  |     -->    | := Node  |
     *             | := Prev  | ----|      | := Prev  | ----->  +------+
     *             +----------+            +----------+         | head |
     *                                                          +------+
     * 3.寻找当前node的prev-node然后开始自旋
     *
     */
    @Override
    public void lock() {
        final Node node = this.node.get();
        node.locked = true;
        // 将tail设置为当前线程的节点，并获取到上一个节点，此操作为原子性操作
        Node pred = this.tail.getAndSet(node);
        this.prev.set(pred);
        // 在前驱节点的locked字段上自旋等待
        while (pred.locked) {
        }
    }

    @Override
    public void unlock() {
        final Node node = this.node.get();
        // 将当前线程节点的locked属性设置为false，使下一个节点成功获取锁
        node.locked = false;
        this.node.set(this.prev.get());
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

    private static class Node {

        private volatile boolean locked;
    }
}
