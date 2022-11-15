package org.gy.demo.vertx.handler;

import static cn.hutool.http.HttpStatus.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.validation.BadRequestException;
import io.vertx.serviceproxy.ServiceException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import org.gy.framework.core.dto.Response;
import org.gy.framework.core.exception.CommonException;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/5 22:43
 */
public abstract class BaseHandler {

    protected <T, R> void invoke(BiConsumer<T, Handler<AsyncResult<R>>> consumer, RoutingContext ctx,
        Function<RoutingContext, T> reqFunc) {
        T req = reqFunc.apply(ctx);
        consumer.accept(req, ar -> serviceResultHandler(ctx, ar));
    }

    protected <R> void invoke(Consumer<Handler<AsyncResult<R>>> consumer, RoutingContext ctx) {
        consumer.accept(ar -> serviceResultHandler(ctx, ar));
    }

    protected <T> void serviceResultHandler(RoutingContext ctx, AsyncResult<T> ar) {
        if (ar.succeeded()) {
            ctx.json(Response.asSuccess(ar.result()));
        } else {
            exceptionHandler(ctx, ar.cause());
        }
    }

    protected void exceptionHandler(RoutingContext ctx, Throwable cause) {
        if (cause instanceof BadRequestException) {
            ctx.json(Response.asError(HTTP_BAD_REQUEST, cause.getMessage()));
        } else if (cause instanceof CommonException) {
            ctx.json(Response.asError((CommonException) cause));
        } else if (cause instanceof ServiceException) {
            ServiceException e = (ServiceException) cause;
            ctx.json(Response.asError(e.failureCode(), e.getMessage()));
        } else {
            ctx.json(Response.asError(HTTP_INTERNAL_ERROR, cause.getMessage()));
        }
    }

}
