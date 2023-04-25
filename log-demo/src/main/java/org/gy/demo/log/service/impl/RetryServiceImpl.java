package org.gy.demo.log.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.gy.demo.log.service.RetryService;
import org.gy.framework.core.dto.Response;
import org.gy.framework.core.exception.CommonException;
import org.gy.framework.core.exception.RetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2023/4/25 10:10
 */
@Slf4j
@Service
public class RetryServiceImpl implements RetryService {

    private static int count = 1;

    @Override
    @Retryable(value = RetryException.class, maxAttempts = 3, backoff = @Backoff(50), recover = "recover2")
    public Response<String> retry(String param) {
        log.info("retry{},message={},throw RetryException in method retry", count, param);
        count++;
        throw new RetryException(100, param);
    }

    @Recover
    public Response<String> recover1(CommonException e, String param) {
        log.info("Default Retry service recover1:code={},msg={},param={}", e.getError(), e.getMessage(), param);
        return Response.asError(e);
    }

    @Recover
    public Response<String> recover2(CommonException e, String param) {
        log.info("Default Retry service recover2:code={},msg={},param={}", e.getError(), e.getMessage(), param);
        return Response.asError(e);
    }
}
