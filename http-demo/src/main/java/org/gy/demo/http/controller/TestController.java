package org.gy.demo.http.controller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.http.service.BaseService;
import org.gy.demo.http.service.FeignService;
import org.gy.framework.core.dto.BaseResponse;
import org.gy.framework.core.dto.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/1/18 16:07
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Resource
    private FeignService feignService;

    @GetMapping("/hello")
    public Object v1() {
        return BaseService.execute(feignService::hello);
    }

    @GetMapping("/v2/hello")
    public Object v2() {
        return BaseService.execute(feignService::helloV2);
    }

    @GetMapping("/v3/hello/{time}")
    public Object v3(@PathVariable("time") long time) throws InterruptedException, ExecutionException {
        CompletableFuture<BaseResponse> future = BaseService.executeAsync(time, feignService::helloV3);
        future.thenAcceptAsync(res -> log.info("v3 res:{}", res));
        return Response.asSuccess(time);
    }


}
