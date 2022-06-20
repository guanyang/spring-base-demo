package org.gy.demo.log.service;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/6/13 10:33
 */
public interface IHelloService {

    void hello();

    default void hello2(){
        System.out.println("hello2");
    }

    default void hello3(){
        System.out.println("hello3");
    }

}
