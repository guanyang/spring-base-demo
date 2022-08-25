package org.gy.demo.log;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/7/26 14:06
 */
public class ConditionTest {

    private static volatile int state = 1;

    private static final int TOTAL = 5;

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        ConditionExt one = new ConditionExt(lock.newCondition(), 1);
        ConditionExt two = new ConditionExt(lock.newCondition(), 2);
        ConditionExt three = new ConditionExt(lock.newCondition(), 3);
        ConditionExt four = new ConditionExt(lock.newCondition(), 4);

        Thread t1 = new Thread(new PrintTask(lock, one, two, 'A'), "ThreadOne");
        Thread t2 = new Thread(new PrintTask(lock, two, three, 'B'), "ThreadTwo");
        Thread t3 = new Thread(new PrintTask(lock, three, four, 'C'), "ThreadThree");
        Thread t4 = new Thread(new PrintTask(lock, four, one, 'D'), "ThreadFour");

        t1.start();
        t2.start();
        t3.start();
        t4.start();
    }

    public static class ConditionExt {

        private final Condition condition;
        private final int state;

        public ConditionExt(Condition condition, int state) {
            this.condition = condition;
            this.state = state;
        }
    }

    public static class PrintTask implements Runnable {

        private final ReentrantLock lock;

        private final ConditionExt current;

        private final ConditionExt next;

        // 打印字符
        private final char printChar;

        public PrintTask(ReentrantLock lock, ConditionExt current, ConditionExt next, char printChar) {
            this.lock = lock;
            this.current = current;
            this.next = next;
            this.printChar = printChar;
        }

        @Override
        public void run() {
            lock.lock();
            try {
                for (int i = 0; i < TOTAL; i++) {
                    while (state != current.state) {
                        try {
                            current.condition.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println(Thread.currentThread().getName() + ":" + printChar);
                    state = next.state;
                    next.condition.signal();
                }
            } finally {
                lock.unlock();
            }
        }
    }

}
