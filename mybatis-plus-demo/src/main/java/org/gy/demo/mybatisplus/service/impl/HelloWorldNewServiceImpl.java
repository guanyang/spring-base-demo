package org.gy.demo.mybatisplus.service.impl;

import org.gy.demo.mybatisplus.entity.HelloWorldNew;
import org.gy.demo.mybatisplus.mapper.HelloWorldNewMapper;
import org.gy.demo.mybatisplus.service.IHelloWorldNewService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 新演示表 服务实现类
 * </p>
 *
 * @author gy
 * @since 2023-07-14
 */
@Service
public class HelloWorldNewServiceImpl extends CommonServiceImpl<HelloWorldNewMapper, HelloWorldNew> implements IHelloWorldNewService {

}
