package org.gy.demo.log.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能描述：应用探活
 *
 * @author gy
 * @version 1.0.0
 */
@RestController
public class PingController {

    @RequestMapping(value = {"/hello", "/api/hello"})
    public Object hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "test");
        result.put("time", System.currentTimeMillis());
        return result;
    }

}
