package org.gy.demo.log;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/8/19 18:01
 */
public class ClhSpinLockTest {

    static int count = 0;

    public static void main(String[] args) {
        int threadNum = 10;
        CyclicBarrier barrier = new CyclicBarrier(threadNum, () -> {
            System.out.println("最后的结果：" + count);
        });
        ClhSpinLock lock = new ClhSpinLock();
        for (int i = 0; i < threadNum; i++) {
            new TaskThread(barrier, lock).start();
        }

    }

    public static class TaskThread extends Thread {

        private final CyclicBarrier cyclicBarrier;

        private final ClhSpinLock lock;

        public TaskThread(CyclicBarrier cyclicBarrier, ClhSpinLock lock) {
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
            for (int i = 0; i < 100000; i++) {
                ++count;
            }
        } finally {
            lock.unlock();
        }
    }

}
