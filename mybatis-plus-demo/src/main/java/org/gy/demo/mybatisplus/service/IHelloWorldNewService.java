package org.gy.demo.mybatisplus.service;

import org.gy.demo.mybatisplus.entity.HelloWorldNew;
import org.gy.demo.mybatisplus.model.TestRequest;
import org.gy.demo.mybatisplus.model.TestResponse;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * <p>
 * 新演示表 服务类
 * </p>
 *
 * @author gy
 * @since 2023-07-14
 */
@Validated
public interface IHelloWorldNewService extends ICommonService<HelloWorldNew> {

    void test10();

    void test11();

    void test20();

    void test21();

    void test22();

    void test30();

    void test31();

    void test32();

    void test40();

    void test41();

    TestResponse hello(@Valid TestRequest request);

}
