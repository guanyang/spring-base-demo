package org.gy.demo.disruptor;

import org.gy.demo.disruptor.core.DisruptorProviderManage;
import org.gy.demo.disruptor.core.consumer.QueueConsumerExecutor;
import org.gy.demo.disruptor.core.consumer.QueueConsumerFactory;
import org.gy.demo.disruptor.core.provider.DisruptorProvider;
import org.junit.jupiter.api.Test;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/6/16 14:18
 */
public class DisruptorTests {

    @Test
    public void test() {
        DisruptorProvider<String> provider = initProvider();
        for (int i = 0; i < 10; i++) {
            String data = "test" + i;
            System.out.println("send data=" + data);
            provider.onData(data);
        }
    }

    public static DisruptorProvider<String> initProvider() {
        QueueConsumerFactory<String> factory = new MyQueueConsumerFactory();
        DisruptorProviderManage<String> providerManage = new DisruptorProviderManage<>(factory);
        providerManage.startup();
        return providerManage.getProvider();
    }

    public static class MyQueueConsumerFactory implements QueueConsumerFactory<String> {


        @Override
        public QueueConsumerExecutor<String> create() {
            return new QueueConsumerExecutor<String>() {
                @Override
                public void run() {
                    System.out.println("Current Thread=" + Thread.currentThread().getName() + "\tdata=" + getData());
                }
            };
        }

        @Override
        public String fixName() {
            return "my_test";
        }
    }

}
