package org.gy.demo.log.service;

import org.gy.framework.core.dto.Response;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2023/4/25 10:01
 */
public interface RetryService {


    Response<String> retry(String param);

}
