package org.gy.demo.configdemo.controller;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/test")
public class TestController {

    @Value("${conf.name}")
    private String name;


    @GetMapping("/conf")
    public Object conf() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("time", System.currentTimeMillis());
        return map;
    }

}
