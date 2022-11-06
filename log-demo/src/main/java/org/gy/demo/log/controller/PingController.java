package org.gy.demo.log.controller;

import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestResponse;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Object hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "test");
        result.put("time", System.currentTimeMillis());
        return result;
    }

    @RequestMapping(value = {"/api/v1/test"})
    public Object test() {
        ForestResponse<Object> response = Forest.get("http://127.0.0.1:8080/hello").execute(ForestResponse.class);
        return response.getResult();
    }

}
