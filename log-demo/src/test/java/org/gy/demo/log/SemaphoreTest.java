package org.gy.demo.log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/7/26 10:45
 */
public class SemaphoreTest {

    public static void main(String[] args) throws InterruptedException {
        int threadNum = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        Semaphore semaphore = new Semaphore(2);
        for (int i = 0; i < threadNum; i++) {
            executor.execute(new Worker(semaphore));
        }
        System.out.println("====main end=====");
    }


    public static class Worker implements Runnable {

        private final Semaphore semaphore;

        public Worker(final Semaphore semaphore) {
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            long random = ThreadLocalRandom.current().nextLong(5000);
            String name = Thread.currentThread().getName() + "-" + random;
            try {
                semaphore.acquire();
                System.out.println(name + " start..." + "\t剩余许可：" + semaphore.availablePermits());
                Thread.sleep(random);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
                System.out.println(name + " end..." + "\t剩余许可：" + semaphore.availablePermits());
            }
        }
    }

}
