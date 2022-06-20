package org.gy.demo.log.service.impl;

import org.gy.demo.log.service.IHelloService;
import org.springframework.stereotype.Service;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/6/13 10:33
 */
@Service
public class CustomHelloService implements IHelloService {

    @Override
    public void hello() {
        System.out.println("Hello World!");
    }
}
