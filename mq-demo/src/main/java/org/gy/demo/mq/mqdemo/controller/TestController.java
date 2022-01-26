package org.gy.demo.mq.mqdemo.controller;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2021/8/16 21:20
 */
@Slf4j
@RequestMapping("/test")
@RestController
public class TestController {

    @GetMapping("/lock")
    public Object lock(TestReq req, String id) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", req.getName());
        result.put("age", req.getAge());
        result.put("id", id);
        return result;
    }


    @Data
    public static class TestReq {

        private String name;

        private Integer age;
    }

}
