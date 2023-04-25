package org.gy.demo.log.service;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Retry;
import com.dtflys.forest.http.ForestResponse;
import org.gy.demo.log.service.support.RetryCondition;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2023/4/25 14:35
 */
@BaseRequest(baseURL = "{baseUrl}")
public interface ForestService {

    @Get("/hello")
    @Retry(maxRetryCount = "3", maxRetryInterval = "10", condition = RetryCondition.class)
    ForestResponse<Object> hello();

}
