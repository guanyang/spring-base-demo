package org.gy.demo.http.controller;

import lombok.extern.slf4j.Slf4j;
import org.gy.demo.http.service.BaseService;
import org.gy.demo.http.service.FeignService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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


}
