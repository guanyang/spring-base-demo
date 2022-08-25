package org.gy.demo.log;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.Getter;
import org.junit.jupiter.api.Test;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/7/26 14:32
 */
public class WaitNotifyTest {

    private final static Callback<Object> done = new Callback<>();

    @Test
    public void testCallBlockingMethod() throws InterruptedException {
        CallThread callThread = new CallThread();
        callThread.start();

        Thread.sleep(500);

        //模拟回调
        done.invoke(true);
        assertEquals(true, done.getData());
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("main start...");
        synchronized (done) {
            System.out.println("main lock");
            CallThread callThread = new CallThread();
            callThread.start();

            Thread.sleep(1000);

            callThread.interrupt();

//            callThread.join();

            System.out.println("main unlock");
        }

        System.out.println("main exit");
    }

    private static class CallThread extends Thread {

        public static void main(String[] args) throws InterruptedException {
            CallThread callThread = new CallThread();
            callThread.start();

            Thread.sleep(1000);
            callThread.interrupt();

            callThread.join();

//            done.invoke(null);

            System.out.println("main exit");

        }


        @Override
        public void run() {
            if (!done.isDone()) {
                synchronized (done) {
                    // 阻塞等待
                    System.out.println("CallThread lock");
                    while (!done.isDone() && !Thread.currentThread().isInterrupted()) {
                        try {
                            System.out.println("CallThread wait start");
                            done.wait();
                            System.out.println("CallThread wait end");
                        } catch (InterruptedException e) {
                            System.out.println("CallThread interrupt");
                            Thread.currentThread().interrupt();
                        }
                    }
                    System.out.println("CallThread unlock");
                }
            }
            System.out.println("CallThread exit");
        }
    }

    public static class InterruptWaitingDemo extends Thread {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // 模拟任务代码
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    // ... 清理操作
                    System.out.println("InterruptWaitingDemo InterruptedException,flag=" + isInterrupted());//false
                    // 重设中断标志位
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("InterruptWaitingDemo exit,flag=" + isInterrupted());
        }

        public static void main(String[] args) {
            InterruptWaitingDemo thread = new InterruptWaitingDemo();
            thread.start();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            thread.interrupt();
            System.out.println("main exit");
        }
    }

    @Getter
    public static class Callback<T> {

        private T data;
        private boolean done = false;

        public void invoke(T data) {
            synchronized (this) {
                this.data = data;
                this.done = true;
                notify();
            }
        }
    }

}
