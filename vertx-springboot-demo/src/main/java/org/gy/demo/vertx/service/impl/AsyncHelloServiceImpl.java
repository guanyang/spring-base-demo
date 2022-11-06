package org.gy.demo.vertx.service.impl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;
import org.gy.demo.vertx.annotation.AsyncService;
import org.gy.demo.vertx.service.HelloService;
import org.gy.demo.vertx.service.async.AsyncHelloService;
import org.gy.demo.vertx.service.dto.User;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/5 20:39
 */
@AsyncService(interfaceClass = AsyncHelloService.class)
public class AsyncHelloServiceImpl extends BaseAsyncServiceImpl implements AsyncHelloService {

    @Autowired
    private HelloService helloService;

    @Override
    public void hello(Handler<AsyncResult<List<Integer>>> resultHandler) {
        asyncHandler(helloService::hello, resultHandler);
    }

    @Override
    public void hello2(Handler<AsyncResult<List<User>>> resultHandler) {
        asyncHandler(helloService::hello2, resultHandler);
    }
}
