package org.gy.demo.log;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicStampedReference;
import lombok.Data;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.Test;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/8/18 16:37
 */
public class AtomicStampedReferenceTest {

    @Test
    public void test1() throws InterruptedException {
        AtomicStampedReference<Integer> stamp1 = new AtomicStampedReference<>(10, 1);
        Thread t1 = new Thread(() -> {
            println(stamp1, "初始化", false);
            boolean result = stamp1.compareAndSet(10, 20, stamp1.getStamp(), stamp1.getStamp() + 1);
            println(stamp1, "第一次", result);
            result = stamp1.compareAndSet(20, 10, stamp1.getStamp(), stamp1.getStamp() + 1);
            println(stamp1, "第二次", result);
        });
        Thread t2 = new Thread(() -> {
            println(stamp1, "初始化", false);
            sleep(100);
            boolean result = stamp1.compareAndSet(10, 30, stamp1.getStamp(), stamp1.getStamp() + 1);
            println(stamp1, "第一次", result);
            result = stamp1.compareAndSet(30, 10, stamp1.getStamp(), stamp1.getStamp() + 1);
            println(stamp1, "第二次", result);
        });
        t1.start();
        t2.start();
        t2.join();
    }

    @Test
    public void test2() throws InterruptedException {
        Item item = new Item().setNum(10).setName("test");
        AtomicStampedReference<Item> stamp1 = new AtomicStampedReference<>(item, 1);
        Thread t1 = new Thread(() -> {
            println(stamp1, "初始化", false);
            boolean result = stamp1.compareAndSet(item, item.setNum(20), stamp1.getStamp(), stamp1.getStamp() + 1);
            println(stamp1, "第一次", result);
            result = stamp1.compareAndSet(item, item.setNum(30), stamp1.getStamp(), stamp1.getStamp() + 1);
            println(stamp1, "第二次", result);
        });
        Thread t2 = new Thread(() -> {
            println(stamp1, "初始化", false);
            sleep(100);
            boolean result = stamp1.compareAndSet(item, item.setNum(40), stamp1.getStamp(), stamp1.getStamp() + 1);
            println(stamp1, "第一次", result);
            result = stamp1.compareAndSet(item, item.setNum(50), stamp1.getStamp(), stamp1.getStamp() + 1);
            println(stamp1, "第二次", result);
        });
        t1.start();
        t2.start();
        t2.join();
    }

    @Data
    @Accessors(chain = true)
    public static class Item {

        private Integer num;
        private String name;

    }


    private static <T> void println(AtomicStampedReference<T> stamp, String desc, boolean result) {
        System.out.println(
            Thread.currentThread().getName() + "\t" + desc + "\t" + "value=" + stamp.getReference() + "\tstamp="
                + stamp.getStamp() + "\tresult=" + result);
    }

    private static void sleep(int randomMills) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(randomMills));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
