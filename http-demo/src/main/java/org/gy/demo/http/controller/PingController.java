package org.gy.demo.http.controller;

import lombok.extern.slf4j.Slf4j;
import org.gy.framework.core.dto.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述：应用探活
 *
 * @author gy
 * @version 1.0.0
 */
@Slf4j
@RestController
public class PingController {

    @RequestMapping(value = {"/hello", "/api/hello"})
    public Object hello() throws InterruptedException {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "test");
        result.put("time", System.currentTimeMillis());
        return result;
    }

    @RequestMapping(value = {"/v2/hello"})
    public Response<Long> helloV2() {
        return Response.asSuccess(System.currentTimeMillis());
    }
}
