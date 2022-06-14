package org.gy.demo.configdemo;

import java.util.function.Consumer;
import org.gy.demo.configdemo.service.IHelloService;
import org.gy.demo.configdemo.service.impl.CustomHelloService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.util.LambdaSafe;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/6/13 10:35
 */
public class TestMain {

    @Test
    public void test() {
        IHelloService service = new CustomHelloService();
        Consumer<IHelloService> consumer = IHelloService::hello;
        LambdaSafe.callback(Consumer.class, consumer, service, null).invoke(c -> c.accept(service));
    }


}
