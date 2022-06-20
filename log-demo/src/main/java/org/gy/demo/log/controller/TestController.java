package org.gy.demo.log.controller;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/test")
public class TestController {


    @GetMapping("/log")
    public Object log() {
        Map<String, Object> map = new HashMap<>(2);
        long now = System.currentTimeMillis();
        map.put("time", now);
        map.put("name", "log-demo");
        log.debug("debug log,time={}", now);
        log.info("info log,time={}", now);
        log.warn("warn log,time={}", now);
        log.error("error log,time={}", now);
        return map;
    }


}
