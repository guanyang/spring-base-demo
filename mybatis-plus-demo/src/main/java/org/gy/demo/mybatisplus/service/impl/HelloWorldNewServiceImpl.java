package org.gy.demo.mybatisplus.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.mybatisplus.entity.HelloWorldNew;
import org.gy.demo.mybatisplus.mapper.HelloWorldNewMapper;
import org.gy.demo.mybatisplus.model.TestRequest;
import org.gy.demo.mybatisplus.model.TestResponse;
import org.gy.demo.mybatisplus.service.IHelloWorldNewService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 新演示表 服务实现类
 * </p>
 *
 * @author gy
 * @since 2023-07-14
 */
@Slf4j
@Service
public class HelloWorldNewServiceImpl extends CommonServiceImpl<HelloWorldNewMapper, HelloWorldNew> implements IHelloWorldNewService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void test10() {
        insertTest("test10");
        //调用异步方法，保证事务能够生效
        SpringUtil.getBean(IHelloWorldNewService.class).test11();
        throw new RuntimeException("test10抛出运行异常");
    }

    @Async
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void test11() {
        insertTest("test11");

        throw new RuntimeException("test11抛出运行异常");

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void test20() {
        insertTest("test20");
        //调用异步方法，保证事务能够生效
        SpringUtil.getBean(IHelloWorldNewService.class).test21();
        SpringUtil.getBean(IHelloWorldNewService.class).test22();
    }

    @Async
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void test21() {
        insertTest("test21");

        throw new RuntimeException("test21抛出运行异常");

    }

    @Async
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void test22() {
        insertTest("test22");

        throw new RuntimeException("test22抛出运行异常");

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void test30() {
        insertTest("test30");
        //调用异步方法，保证事务能够生效
        SpringUtil.getBean(IHelloWorldNewService.class).test31();

    }

    @Async
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void test31() {
        insertTest("test31");
        //调用异步方法，保证事务能够生效
        SpringUtil.getBean(IHelloWorldNewService.class).test32();
        throw new RuntimeException("test31抛出运行异常");

    }

    @Async
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void test32() {
        insertTest("test32");
    }

    @Async
    @Override
    public void test40() {
        log.info("{}的线程：{}", "test40", Thread.currentThread().getName());
//        test41();
        SpringUtil.getBean(IHelloWorldNewService.class).test41();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void test41() {
        insertTest("test41");
        throw new RuntimeException("test41抛出运行异常");
    }

    @Override
    public TestResponse hello(TestRequest request) {
        TestResponse response = new TestResponse();
        response.setName(request.getName());
        response.setAge(request.getAge());
        return response;
    }

    private void insertTest(String desc) {
        log.info("{}的线程：{}", desc, Thread.currentThread().getName());
        HelloWorldNew user = new HelloWorldNew().setName(desc + "-" + System.currentTimeMillis());
        save(user);
    }

}
