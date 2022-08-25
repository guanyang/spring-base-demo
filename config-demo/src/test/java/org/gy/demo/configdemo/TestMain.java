package org.gy.demo.configdemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.configdemo.service.IHelloService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.util.LambdaSafe;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/6/13 10:35
 */
@Slf4j
public class TestMain {

    @Test
    public void test() {
        IHelloService service = new MyCustomHelloService();

        Consumer<IHelloService> consumer = IHelloService::hello;
        Consumer<IHelloService> consumer2 = IHelloService::hello2;
        Consumer<IHelloService> consumer3 = IHelloService::hello3;
        List<Consumer<IHelloService>> list = new ArrayList<>();
        list.add(consumer);
        list.add(consumer2);
        list.add(consumer3);

        LambdaSafe.callbacks(Consumer.class, list, service, null).invoke(c -> c.accept(service));
    }

    @Test
    public void test2() {
        IHelloService service = new MyCustomHelloService();

        BiFunction<IHelloService, String, String> function = IHelloService::hello4;
        List<BiFunction<IHelloService, String, String>> list = new ArrayList<>();
        list.add(function);

        LambdaSafe.callbacks(BiFunction.class, list, service, null).invokeAnd(c -> c.apply(service, "test"))
            .forEach(System.out::println);
    }

    public class MyCustomHelloService implements IHelloService {

        @Override
        public void hello() {
            System.out.println("MyCustomHelloService");
        }
    }


}
