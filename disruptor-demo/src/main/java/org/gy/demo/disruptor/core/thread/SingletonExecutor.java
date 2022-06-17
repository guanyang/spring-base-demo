package org.gy.demo.disruptor.core.thread;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/6/16 10:49
 */
public class SingletonExecutor extends ThreadPoolExecutor {

    public SingletonExecutor(final ThreadFactory factory) {
        super(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), factory);
    }
}
