package org.gy.demo.http.service;

import feign.FeignException;
import feign.FeignException.FeignClientException;
import org.gy.framework.core.dto.BaseResponse;
import org.gy.framework.core.dto.Response;
import org.gy.framework.core.exception.CommonException;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2023/5/25 15:23
 */
public interface BaseService {

    String SERVER_ERROR = "Internal Server Error";
    String CLIENT_ERROR = "Invalid Request Error";

    static <T, R> BaseResponse execute(T req, Function<T, R> responseFunction) {
        return execute(() -> responseFunction.apply(req));
    }

    static <R> BaseResponse execute(Supplier<R> responseSupplier) {
        try {
            R res = responseSupplier.get();
            return responseHandler(res);
        } catch (Throwable e) {
            return exceptionHandler(e);
        }
    }

    static <R> BaseResponse responseHandler(R res) {
        if (res instanceof BaseResponse) {
            return (BaseResponse) res;
        }
        return Response.asSuccess(res);
    }

    static BaseResponse exceptionHandler(Throwable cause) {
        if (cause instanceof CommonException) {
            return Response.asError((CommonException) cause);
        } else if (cause instanceof FeignClientException) {
            FeignClientException e = (FeignClientException) cause;
            return Response.asError(e.status(), CLIENT_ERROR);
        } else if (cause instanceof FeignException) {
            FeignException e = (FeignException) cause;
            return Response.asError(e.status(), e.getMessage());
        } else {
            String message = Optional.ofNullable(cause).map(Throwable::getMessage).orElse(SERVER_ERROR);
            return Response.asError(HTTP_INTERNAL_ERROR, SERVER_ERROR);
        }
    }

}
