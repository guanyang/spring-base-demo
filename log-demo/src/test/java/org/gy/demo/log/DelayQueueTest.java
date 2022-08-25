package org.gy.demo.log;

import java.util.Objects;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Data;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/7/29 10:00
 */
public class DelayQueueTest {

    public static void main(String[] args) throws InterruptedException {
        DelayQueue<TaskItem> queue = new DelayQueue<>();
        ExecutorService service = Executors.newFixedThreadPool(1);
        service.execute(() -> {
            while (true) {
                try {
                    TaskItem item = queue.take();
                    System.out.println("task info:" + item);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        for (int i = 0; i < 10; i++) {
            TaskItem item = new TaskItem(1000 * (i + 1));
            queue.add(item);
        }
        System.out.println("main exit");

    }


    @Data
    public static class TaskItem implements Delayed {

        private static final AtomicLong sequencer = new AtomicLong(0);

        private final long timeoutMs;

        private final long n;

        private final long time;

        public TaskItem(long timeoutMs) {
            this.timeoutMs = timeoutMs;
            this.time = System.currentTimeMillis() + timeoutMs;
            this.n = sequencer.incrementAndGet();
        }

        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(time - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            TaskItem item = (TaskItem) o;
            return Long.compare(this.time, item.time);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            TaskItem taskItem = (TaskItem) o;
            return timeoutMs == taskItem.timeoutMs && n == taskItem.n && time == taskItem.time;
        }

        @Override
        public int hashCode() {
            return Objects.hash(timeoutMs, n, time);
        }
    }

}
