package org.gy.demo.dubbo.api.service;

import org.gy.demo.dubbo.api.dto.TestRequest;
import org.gy.demo.dubbo.api.dto.TestResponse;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 */
public interface TestService {

    TestResponse hello(TestRequest request);

}
