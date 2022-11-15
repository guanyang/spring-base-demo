package org.gy.demo.vertx.handler;


import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.vertx.annotation.RouteMapping;
import org.gy.framework.core.exception.CommonException;
import org.springframework.stereotype.Component;

/**
 * 功能描述：全局异常handler
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/14 17:20
 */
@Slf4j
@Component
@RouteMapping
public class GlobalExceptionHandler extends BaseHandler {

    @RouteMapping(exception = true, statusCode = 400)
    public Handler<RoutingContext> badRequestHandler() {
        return ctx -> exceptionHandler(ctx, ctx.failure());
    }

    @RouteMapping(exception = true, statusCode = {500, 501, 502, 503, 504, 505})
    public Handler<RoutingContext> internalErrorHandler() {
        return ctx -> exceptionHandler(ctx, ctx.failure());
    }

    @RouteMapping(exception = true, statusCode = 404)
    public Handler<RoutingContext> notFoundHandler() {
        return ctx -> exceptionHandler(ctx, new CommonException(404, "http not found"));
    }

    @RouteMapping(exception = true, statusCode = 405)
    public Handler<RoutingContext> notSupportHandler() {
        return ctx -> exceptionHandler(ctx, new CommonException(405, "http method not match"));
    }

    @RouteMapping(exception = true, statusCode = {406, 415})
    public Handler<RoutingContext> contentTypeErrorHandler() {
        return ctx -> exceptionHandler(ctx, new CommonException(405, "Content-type not match"));
    }

}
