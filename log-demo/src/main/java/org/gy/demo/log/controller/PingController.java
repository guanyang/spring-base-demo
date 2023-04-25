package org.gy.demo.log.controller;

import static org.gy.framework.core.exception.CommonErrorCode.SYS_SERVICE_ERROR;

import com.dtflys.forest.Forest;
import com.dtflys.forest.http.ForestResponse;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.log.service.ForestService;
import org.gy.framework.core.dto.Response;
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

    @Resource
    private ForestService forestService;

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

    @RequestMapping(value = {"/api/v2/test"})
    public Object test2() {
        ForestResponse<Object> response = forestService.hello();
        return wrapResponse(response);
    }

    private static <R> Response<R> wrapResponse(ForestResponse<R> res) {
        if (res.isError()) {
            return Response.asError(SYS_SERVICE_ERROR.getError(), SYS_SERVICE_ERROR.getMsg(), res.getResult());
        } else {
            return Response.asSuccess(res.getResult());
        }
    }

}
