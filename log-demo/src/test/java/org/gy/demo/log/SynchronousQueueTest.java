package org.gy.demo.log;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/7/27 18:03
 */
public class SynchronousQueueTest {

    public static void main(String[] args) {
        SynchronousQueue<Integer> queue = new SynchronousQueue<Integer>();
        new Consumer(queue).start();
        new Producer(queue).start();
    }

    static class Producer extends Thread {

        private final SynchronousQueue<Integer> queue;

        public Producer(SynchronousQueue<Integer> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                int rand = ThreadLocalRandom.current().nextInt(1000);
                System.out.println("生产了一个产品：" + rand);
                System.out.println("等待三秒后运送出去...");
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                queue.offer(rand);
                System.out.println("产品生成完成：" + rand);
            }
        }
    }

    static class Consumer extends Thread {

        private final SynchronousQueue<Integer> queue;

        public Consumer(SynchronousQueue<Integer> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    System.out.println("消费了一个产品:" + queue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("------------------------------------------");
            }
        }
    }

}
