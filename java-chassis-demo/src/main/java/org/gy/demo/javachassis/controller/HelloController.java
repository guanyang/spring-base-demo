package org.gy.demo.javachassis.controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 功能描述：应用探活
 *
 * @author gy
 * @version 1.0.0
 */
@RestSchema(schemaId = "hello")
@RequestMapping(path = "/")
public class HelloController {

    @RequestMapping(value = {"/hello", "/api/hello"})
    public Object hello() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "test");
        result.put("time", System.currentTimeMillis());
        return result;
    }

}
