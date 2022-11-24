package org.gy.demo.dubbo.consumer.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.gy.demo.dubbo.api.dto.TestRequest;
import org.gy.demo.dubbo.api.dto.TestResponse;
import org.gy.demo.dubbo.api.service.TestService;
import org.gy.framework.core.dto.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    @DubboReference(check = false, timeout = 1000, retries = 0, version = "1.0.0")
    private TestService testService;

    @GetMapping("/hello")
    public Object hello(TestRequest request) {
        Response<TestResponse> hello = testService.hello(request);
        return hello;
    }
}
