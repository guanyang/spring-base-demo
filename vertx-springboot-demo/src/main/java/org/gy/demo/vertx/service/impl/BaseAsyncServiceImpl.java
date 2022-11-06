package org.gy.demo.vertx.service.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.serviceproxy.ServiceException;
import java.util.function.Function;
import java.util.function.Supplier;
import org.gy.framework.core.dto.Response;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/5 23:23
 */
public abstract class BaseAsyncServiceImpl {

    protected <R> void asyncHandler(Supplier<Response<R>> responseSupplier, Handler<AsyncResult<R>> resultHandler) {
        Response<R> response = responseSupplier.get();
        responseHandler(response, resultHandler);
    }

    protected <T, R> void asyncHandler(Function<T, Response<R>> responseFunction, T req,
        Handler<AsyncResult<R>> resultHandler) {
        Response<R> response = responseFunction.apply(req);
        responseHandler(response, resultHandler);
    }

    private static <R> void responseHandler(Response<R> response, Handler<AsyncResult<R>> resultHandler) {
        if (response.success()) {
            resultHandler.handle(Future.succeededFuture(response.getData()));
        } else {
            resultHandler.handle(Future.failedFuture(new ServiceException(response.getError(), response.getMsg())));
        }
    }

}
