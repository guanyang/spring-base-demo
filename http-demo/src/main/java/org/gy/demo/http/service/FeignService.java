package org.gy.demo.http.service;

import org.gy.demo.http.config.DemoClientOkHttpConfig;
import org.gy.framework.core.dto.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2023/5/24 19:50
 */
@FeignClient(name = "demoClient", url = "${feign.demoClientCfg.url}", configuration = DemoClientOkHttpConfig.class)
public interface FeignService {

    @RequestMapping("/hello")
    Object hello();

    @RequestMapping("/v2/hello")
    Response<Long> helloV2();

}
