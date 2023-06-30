package org.gy.demo.configdemo.service;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/6/13 10:33
 */
public interface IHelloService {

    void hello();

    default void hello2() {
        System.out.println("hello2");
    }

    default void hello3() {
        System.out.println("hello3");
    }

    default String hello4(String msg) {
        return String.join("@", String.valueOf(System.currentTimeMillis()), msg);
    }

    default String hello5() {
        return String.join("@", String.valueOf(System.currentTimeMillis()), "hello5");
    }

}
