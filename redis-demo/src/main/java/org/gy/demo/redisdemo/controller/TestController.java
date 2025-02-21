package org.gy.demo.redisdemo.controller;

import com.baomidou.lock.annotation.Lock4j;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.redisdemo.handler.lock.DistributedLock;
import org.gy.demo.redisdemo.handler.lock.DistributedLockAction;
import org.gy.demo.redisdemo.handler.lock.DistributedLockAction.LockResult;
import org.gy.demo.redisdemo.handler.lock.DistributedLockException;
import org.gy.demo.redisdemo.handler.lock.support.RedisDistributedLock;
import org.gy.demo.redisdemo.model.User;
import org.gy.framework.core.dto.Response;
import org.gy.framework.idempotent.annotation.Idempotent;
import org.gy.framework.limit.annotation.LimitCheck;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/1/18 16:07
 */
@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @GetMapping("/limit")
    @LimitCheck(key = "'GY:LIMIT:TEST:' + #key", limit = 1, time = 10, type = "custom")
    public Response test(String key) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("key", key);
        map.put("time", System.currentTimeMillis());
        return Response.asSuccess(map);
    }

    @GetMapping("/idempotent")
    @Idempotent(timeout = 10)
    public Response idempotent(String key) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("key", key);
        map.put("time", System.currentTimeMillis());
        return Response.asSuccess(map);
    }

    @GetMapping("/hello")
    public String hello(String key) {
        return String.valueOf(System.currentTimeMillis());
    }


    @GetMapping("/lock4j")
    @Lock4j(keys = {"#user.id"}, expire = 60000, acquireTimeout = 500)
    public Response<User> testLock4j(User user) throws InterruptedException {
        user.setTime(System.currentTimeMillis());
        Thread.sleep(2000);
        return Response.asSuccess(user);
    }

    @GetMapping("/lock")
    public Response<User> testLock(User user) {
        DistributedLock lock = new RedisDistributedLock(stringRedisTemplate, "GY:LOCK:TEST:" + user.getId(), 60000);
        LockResult<User> result = DistributedLockAction.execute(lock, 200, 50, () -> {
            user.setTime(System.currentTimeMillis());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                // ignore
            }
            return user;
        });
        if (!result.success()) {
            throw new DistributedLockException("lock fail");
        }
        return Response.asSuccess(result.getData());
    }

}
