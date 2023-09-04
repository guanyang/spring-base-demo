package org.gy.demo.redisdemo.controller;

import lombok.extern.slf4j.Slf4j;
import org.gy.framework.core.dto.Response;
import org.gy.framework.limit.annotation.LimitCheck;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/1/18 16:07
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {


    @GetMapping("/limit")
    @LimitCheck(key = "'GY:LIMIT:TEST:' + #key", limit = 1, time = 10, type = "custom")
    public Response test(String key) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("key", key);
        map.put("time", System.currentTimeMillis());
        return Response.asSuccess(map);
    }

    @GetMapping("/hello")
    public String hello(String key) {
        return String.valueOf(System.currentTimeMillis());
    }

}
