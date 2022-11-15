package org.gy.demo.vertx.handler;

import static cn.hutool.http.HttpStatus.HTTP_BAD_REQUEST;
import static cn.hutool.http.HttpStatus.HTTP_INTERNAL_ERROR;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.vertx.annotation.RouteMapping;
import org.gy.demo.vertx.annotation.support.RouteMethod;
import org.gy.framework.core.dto.Response;
import org.gy.framework.core.exception.BizException;
import org.gy.framework.core.exception.SysException;
import org.springframework.stereotype.Component;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/3 17:02
 */
@Slf4j
@Component
@RouteMapping("/")
public class TestHandler extends BaseHandler {

    @RouteMapping(value = "/hello", method = RouteMethod.GET)
    public Handler<RoutingContext> hello() {
        return ctx -> {
            String param = ctx.request().getParam("key", "defaultValue");
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("msg", "ok");
            result.put("data", param);
            ctx.json(result);
        };
    }

    @RouteMapping(value = "/api/v1/test1", method = RouteMethod.GET)
    public Handler<RoutingContext> test1() {
        return ctx -> {
            Response<Long> response = new Response<>();
            response.setData(System.currentTimeMillis());
            ctx.json(response);
            log.info("TestHandler exec success");
        };
    }

    @RouteMapping(value = "/api/v1/exception1", method = RouteMethod.GET)
    public Handler<RoutingContext> exception1() {
        return ctx -> ctx.fail(HTTP_BAD_REQUEST, new BizException());
    }

    @RouteMapping(value = "/api/v1/exception2", method = RouteMethod.GET)
    public Handler<RoutingContext> exception2() {
        return ctx -> ctx.fail(HTTP_INTERNAL_ERROR, new SysException());
    }
}
