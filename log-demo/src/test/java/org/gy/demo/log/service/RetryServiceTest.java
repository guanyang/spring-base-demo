package org.gy.demo.log.service;

import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.log.DemoApplication;
import org.gy.framework.core.dto.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2023/4/25 10:17
 */
@Slf4j
@SpringBootTest(classes = DemoApplication.class)
class RetryServiceTest {

    @Resource
    private RetryService retryService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void retry() {
        String message = "retry";
        Response<String> response = retryService.retry(message);
        Assertions.assertEquals(message, response.getMsg());
    }
}