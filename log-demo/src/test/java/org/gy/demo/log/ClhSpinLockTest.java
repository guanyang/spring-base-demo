package org.gy.demo.log;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import org.junit.jupiter.api.Test;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/8/19 18:01
 */
public class ClhSpinLockTest {

    static int count = 0;

    static int total = 100000;
    static int threadNum = 10;

    @Test
    public void test1() {
        CyclicBarrier barrier = new CyclicBarrier(threadNum, () -> {
            System.out.println("最后的结果：" + count);
            assertEquals(count, total * threadNum);
        });
        Lock lock = new ClhSpinLock();
        for (int i = 0; i < threadNum; i++) {
            new TaskThread(barrier, lock).start();
        }
    }

    @Test
    public void test2() {
        CyclicBarrier barrier = new CyclicBarrier(threadNum, () -> {
            System.out.println("最后的结果：" + count);
            assertEquals(count, total * threadNum);
        });
        Lock lock = new McsSpinLock();
        for (int i = 0; i < threadNum; i++) {
            new TaskThread(barrier, lock).start();
        }
    }

    @Test
    public void test3() {
        CyclicBarrier barrier = new CyclicBarrier(threadNum, () -> {
            System.out.println("最后的结果：" + count);
            assertEquals(count, total * threadNum);
        });
        Lock lock = new SimpleSpinLock();
        for (int i = 0; i < threadNum; i++) {
            new TaskThread(barrier, lock).start();
        }
    }

    @Test
    public void test4() {
        CyclicBarrier barrier = new CyclicBarrier(threadNum, () -> {
            System.out.println("最后的结果：" + count);
            assertEquals(count, total * threadNum);
        });
        Lock lock = new ReentrantSpinLock();
        for (int i = 0; i < threadNum; i++) {
            new TaskThread(barrier, lock).start();
        }
    }

    public static class TaskThread extends Thread {

        private final CyclicBarrier cyclicBarrier;

        private final Lock lock;

        public TaskThread(CyclicBarrier cyclicBarrier, Lock lock) {
            this.cyclicBarrier = cyclicBarrier;
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                testLock(lock);
                cyclicBarrier.await();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void testLock(Lock lock) {
        try {
            lock.lock();
            for (int i = 0; i < total; i++) {
                ++count;
            }
        } finally {
            lock.unlock();
        }
    }

}
