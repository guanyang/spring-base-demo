package org.gy.demo.dubbo.provider.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.gy.demo.dubbo.api.dto.TestRequest;
import org.gy.demo.dubbo.api.dto.TestResponse;
import org.gy.demo.dubbo.api.service.TestService;
import org.gy.framework.core.dto.Response;
import org.springframework.stereotype.Component;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 */
@Slf4j
@Component
@DubboService(version = "1.0.0", timeout = 3000, validation = "true", filter = {"customValidationFilter",
    "-validation"})
public class TestServiceImpl implements TestService {

    @Override
    public Response<TestResponse> hello(TestRequest request) {
        TestResponse response = new TestResponse();
        response.setName(request.getName());
        response.setAge(request.getAge());
        response.setTime(System.currentTimeMillis());
        return Response.asSuccess(response);
    }
}
