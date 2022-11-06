package org.gy.demo.vertx.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.gy.demo.vertx.service.HelloService;
import org.gy.demo.vertx.service.dto.User;
import org.gy.framework.core.dto.Response;
import org.springframework.stereotype.Service;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/11/5 20:35
 */
@Service
public class HelloServiceImpl implements HelloService {

    @Override
    public Response<List<Integer>> hello() {
        List<Integer> result = new ArrayList<>();
        result.add(1);
        result.add(2);
        return Response.asSuccess(result);
    }

    @Override
    public Response<List<User>> hello2() {
        List<User> result = new ArrayList<>();
        User user1 = new User();
        user1.setName("test1");
        user1.setAge(20);
        result.add(user1);

        User user2 = new User();
        user2.setName("test2");
        user2.setAge(22);
        result.add(user2);
        return Response.asSuccess(result);
    }
}
