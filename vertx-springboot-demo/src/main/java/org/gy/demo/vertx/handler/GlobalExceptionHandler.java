package org.gy.demo.vertx.handler;


import static cn.hutool.http.HttpStatus.HTTP_BAD_REQUEST;
import static cn.hutool.http.HttpStatus.HTTP_INTERNAL_ERROR;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.vertx.annotation.RouteMapping;
import org.springframework.stereotype.Component;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/14 17:20
 */
@Slf4j
@Component
@RouteMapping
public class GlobalExceptionHandler extends BaseHandler {

    @RouteMapping(exception = true, statusCode = {HTTP_BAD_REQUEST, HTTP_INTERNAL_ERROR})
    public Handler<RoutingContext> exception1() {
        return ctx -> exceptionHandler(ctx, ctx.failure());
    }

}
