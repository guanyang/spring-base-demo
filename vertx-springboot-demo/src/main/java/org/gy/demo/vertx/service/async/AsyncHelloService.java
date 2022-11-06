package org.gy.demo.vertx.service.async;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import java.util.List;
import org.gy.demo.vertx.service.dto.User;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/5 20:37
 */
@ProxyGen
public interface AsyncHelloService {

    void hello(Handler<AsyncResult<List<Integer>>> resultHandler);

    void hello2(Handler<AsyncResult<List<User>>> resultHandler);

}
