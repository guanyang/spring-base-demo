package org.gy.demo.javachassis.service.impl;

import org.gy.demo.javachassis.service.IHelloService;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/6/13 10:33
 */
public class CustomHelloService implements IHelloService {

    @Override
    public void hello() {
        System.out.println("Hello World!");
    }
}
