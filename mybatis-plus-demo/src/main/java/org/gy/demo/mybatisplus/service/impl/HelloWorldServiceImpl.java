package org.gy.demo.mybatisplus.service.impl;

import org.gy.demo.mybatisplus.entity.HelloWorld;
import org.gy.demo.mybatisplus.mapper.HelloWorldMapper;
import org.gy.demo.mybatisplus.service.IHelloWorldService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 简单演示表 服务实现类
 * </p>
 *
 * @author gy
 * @since 2021-11-29
 */
@Service
public class HelloWorldServiceImpl extends ServiceImpl<HelloWorldMapper, HelloWorld> implements IHelloWorldService {

}
