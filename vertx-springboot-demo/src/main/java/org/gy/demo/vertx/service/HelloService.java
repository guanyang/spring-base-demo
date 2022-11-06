package org.gy.demo.vertx.service;

import java.util.List;
import org.gy.demo.vertx.service.dto.User;
import org.gy.framework.core.dto.Response;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/6/13 10:33
 */
public interface HelloService {

    Response<List<Integer>> hello();

    Response<List<User>> hello2();

}
