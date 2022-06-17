/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gy.demo.disruptor.core;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.IgnoreExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.gy.demo.disruptor.core.consumer.QueueConsumer;
import org.gy.demo.disruptor.core.consumer.QueueConsumerFactory;
import org.gy.demo.disruptor.core.event.DataEvent;
import org.gy.demo.disruptor.core.event.DisruptorEventFactory;
import org.gy.demo.disruptor.core.event.OrderlyDisruptorEventFactory;
import org.gy.demo.disruptor.core.provider.DisruptorProvider;
import org.gy.demo.disruptor.core.thread.DisruptorThreadFactory;
import org.gy.demo.disruptor.core.thread.OrderlyExecutor;

/**
 * DisruptorProviderManage. disruptor provider manager.
 *
 * @param <T> the type parameter
 */
public class DisruptorProviderManage<T> {

    public static final Integer DEFAULT_SIZE = 4096 << 1 << 1;

    private static final Integer DEFAULT_CONSUMER_SIZE = Runtime.getRuntime().availableProcessors() << 1;

    private final Integer size;

    private final Integer consumerSize;

    private final QueueConsumerFactory<T> consumerFactory;

    private DisruptorProvider<T> provider;

    private OrderlyExecutor executor;

    /**
     * Instantiates a new Disruptor provider manage.
     *
     * @param consumerFactory the consumer factory
     * @param ringBufferSize the size
     */
    public DisruptorProviderManage(final QueueConsumerFactory<T> consumerFactory, final Integer ringBufferSize) {
        this(consumerFactory, DEFAULT_CONSUMER_SIZE, ringBufferSize);
    }

    /**
     * Instantiates a new Disruptor provider manage.
     *
     * @param consumerFactory the consumer factory
     */
    public DisruptorProviderManage(final QueueConsumerFactory<T> consumerFactory) {
        this(consumerFactory, DEFAULT_CONSUMER_SIZE, DEFAULT_SIZE);
    }

    /**
     * Instantiates a new Disruptor provider manage.
     *
     * @param consumerFactory the consumer factory
     * @param consumerSize the consumer size
     * @param ringBufferSize the ringBuffer size
     */
    public DisruptorProviderManage(final QueueConsumerFactory<T> consumerFactory, final int consumerSize,
        final int ringBufferSize) {
        this.consumerFactory = consumerFactory;
        this.size = ringBufferSize;
        this.consumerSize = consumerSize;
    }

    /**
     * start disruptor.
     */
    public void startup() {
        this.startup(false);
    }

    /**
     * start disruptor..
     *
     * @param isOrderly the orderly Whether to execute sequentially.
     */
    @SuppressWarnings("all")
    public void startup(final boolean isOrderly) {
        this.executor = new OrderlyExecutor(isOrderly, consumerSize, consumerSize, 0, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), DisruptorThreadFactory.create("consumer", false),
            new ThreadPoolExecutor.AbortPolicy());
        int newConsumerSize = this.consumerSize;
        EventFactory eventFactory;
        if (isOrderly) {
            newConsumerSize = 1;
            eventFactory = new OrderlyDisruptorEventFactory<>();
        } else {
            eventFactory = new DisruptorEventFactory<>();
        }
        Disruptor<DataEvent<T>> disruptor = new Disruptor<>(eventFactory, size,
            DisruptorThreadFactory.create("provider_" + consumerFactory.fixName(), false), ProducerType.MULTI,
            new BlockingWaitStrategy());
        QueueConsumer<T>[] consumers = new QueueConsumer[newConsumerSize];
        for (int i = 0; i < newConsumerSize; i++) {
            consumers[i] = new QueueConsumer<>(executor, consumerFactory);
        }
        disruptor.handleEventsWithWorkerPool(consumers);
        disruptor.setDefaultExceptionHandler(new IgnoreExceptionHandler());
        RingBuffer<DataEvent<T>> ringBuffer = disruptor.start();
        provider = new DisruptorProvider<>(ringBuffer, disruptor, isOrderly);
    }

    /**
     * Gets provider.
     *
     * @return the provider
     */
    public DisruptorProvider<T> getProvider() {
        return provider;
    }
}
