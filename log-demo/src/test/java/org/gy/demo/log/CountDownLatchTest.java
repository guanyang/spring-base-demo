package org.gy.demo.log;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/7/26 10:19
 */
public class CountDownLatchTest {

    private static final Set<String> result = new ConcurrentSkipListSet<>();

    public static void main(String[] args) throws InterruptedException {
        int threadNum = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        CountDownLatch latch = new CountDownLatch(threadNum);
        for (int i = 0; i < threadNum; i++) {
            executor.execute(new Worker(latch));
        }
        latch.await();
        System.out.println("=========");
        result.forEach(System.out::println);
    }

    public static class Worker implements Runnable {

        private final CountDownLatch latch;

        public Worker(final CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                long random = ThreadLocalRandom.current().nextLong(3000);
                String name = Thread.currentThread().getName() + "-" + random;
                System.out.println(name + " start...");
                Thread.sleep(random);
                result.add(name);
                System.out.println(name + " end...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }
    }

}
