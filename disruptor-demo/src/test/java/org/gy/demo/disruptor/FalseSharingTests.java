package org.gy.demo.disruptor;

import java.lang.reflect.Array;
import org.junit.jupiter.api.Test;
import sun.misc.Contended;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/6/16 20:37
 */
public class FalseSharingTests {


    @Test
    public void testWithPadding() throws Exception {
        for (int i = 1; i < 10; i++) {
            System.gc();
            final long start = System.currentTimeMillis();
            runTest(i, ValuePadding.class);
            System.out.println("ValuePadding: Thread num " + i + " duration = " + (System.currentTimeMillis() - start));
        }
    }

    @Test
    public void testWithNoPadding() throws Exception {
        for (int i = 1; i < 10; i++) {
            System.gc();
            final long start = System.currentTimeMillis();
            runTest(i, ValueNoPadding.class);
            System.out.println(
                "ValueNoPadding: Thread num " + i + " duration = " + (System.currentTimeMillis() - start));
        }
    }

    //JVM 添加 -XX:-RestrictContended 参数后 @sun.misc.Contended 注解才有效
    @Test
    public void testWithContended() throws Exception {
        for (int i = 1; i < 10; i++) {
            System.gc();
            final long start = System.currentTimeMillis();
            runTest(i, ValueContended.class);
            System.out.println(
                "ValueContended: Thread num " + i + " duration = " + (System.currentTimeMillis() - start));
        }
    }

    public static <T extends ValueHolder> void runTest(int threadNum, Class<T> clazz) throws Exception {
        T[] values = (T[]) Array.newInstance(clazz, threadNum);
        for (int i = 0; i < values.length; i++) {
            values[i] = clazz.newInstance();
        }
        Thread[] threads = new Thread[threadNum];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new FalseSharing<T>(i, values));
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }


    public static class FalseSharing<T extends ValueHolder> implements Runnable {

        private final static long ITERATIONS = 500L * 1000L * 100L;
        private int arrayIndex = 0;

        private T[] values;

        public FalseSharing(final int arrayIndex, final T[] values) {
            this.arrayIndex = arrayIndex;
            this.values = values;
        }

        @Override
        public void run() {
            long i = ITERATIONS + 1;
            while (0 != --i) {
                values[arrayIndex].setValue(0);
            }
        }

    }

    public final static class ValuePadding implements ValueHolder {

        protected long p1, p2, p3, p4, p5, p6, p7;
        protected volatile long value = 0L;
        protected long p9, p10, p11, p12, p13, p14, p15;

        @Override
        public long getValue() {
            return value;
        }

        @Override
        public void setValue(long value) {
            this.value = value;
        }
    }

    public final static class ValueContended implements ValueHolder {

        @Contended
        protected volatile long value = 0L;

        @Override
        public long getValue() {
            return value;
        }

        @Override
        public void setValue(long value) {
            this.value = value;
        }
    }

    public final static class ValueNoPadding implements ValueHolder {

        // protected long p1, p2, p3, p4, p5, p6, p7;
        protected volatile long value = 0L;
        // protected long p9, p10, p11, p12, p13, p14, p15;

        @Override
        public long getValue() {
            return value;
        }

        @Override
        public void setValue(long value) {
            this.value = value;
        }
    }

    interface ValueHolder {

        long getValue();

        void setValue(long value);
    }


}
