package org.gy.demo.disruptor.core.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/6/16 10:53
 */
public class DisruptorThreadFactory implements ThreadFactory {

    private static final AtomicLong THREAD_NUMBER = new AtomicLong(1);

    private static final ThreadGroup THREAD_GROUP = new ThreadGroup("default-disruptor");

    private final boolean daemon;

    private final String namePrefix;

    private final int priority;

    private DisruptorThreadFactory(final String namePrefix, final boolean daemon, final int priority) {
        this.namePrefix = namePrefix;
        this.daemon = daemon;
        this.priority = priority;
    }

    public static ThreadFactory create(final String namePrefix, final boolean daemon) {
        return create(namePrefix, daemon, Thread.NORM_PRIORITY);
    }

    public static ThreadFactory create(final String namePrefix, final boolean daemon, final int priority) {
        return new DisruptorThreadFactory(namePrefix, daemon, priority);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(THREAD_GROUP, r,
            THREAD_GROUP.getName() + "-" + namePrefix + "-" + THREAD_NUMBER.getAndIncrement());
        thread.setDaemon(daemon);
        thread.setPriority(priority);
        return thread;
    }
}
