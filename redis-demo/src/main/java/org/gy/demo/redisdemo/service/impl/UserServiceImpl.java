package org.gy.demo.redisdemo.service.impl;

import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.redisdemo.model.User;
import org.gy.demo.redisdemo.model.User.Info;
import org.gy.demo.redisdemo.service.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    public static final String CACHE_NAME = "gy:test:user#300";

    @Override
    @Cacheable(cacheNames = CACHE_NAME, key = "#id", condition = "#a0>0", sync = true)
    public User get(Integer id) {
        //模拟数据查询
        log.info("[UserService]get信息:id={}", id);
        User user = new User();
        user.setId(id).setName("test" + id).setTime(System.currentTimeMillis());
        Info info = new Info();
        info.setAddress("addr" + id);
        user.setInfo(info);
        return user;
    }

    @Override
    @CachePut(cacheNames = CACHE_NAME, key = "#result.id")
    public User save(User user) {
        //模拟数据更新
        log.info("[UserService]save信息:user={}", user);
        user.setTime(System.currentTimeMillis());
        return user;
    }

    @Override
    @CacheEvict(cacheNames = CACHE_NAME, key = "#id")
    public void delete(Integer id) {
        //模拟数据删除
        log.info("[UserService]delete信息:id={}", id);
    }

    @Override
    @Caching(cacheable = {@Cacheable(cacheNames = CACHE_NAME, key = "#name")}, put = {
        @CachePut(cacheNames = CACHE_NAME, key = "#result.id")})
    public User getByName(String name) {
        //模拟数据查询
        log.info("[UserService]getByName信息:name={}", name);
        User user = new User();
        int id = ThreadLocalRandom.current().nextInt(100);
        user.setId(id).setName(name).setTime(System.currentTimeMillis());
        Info info = new Info();
        info.setAddress("addr" + id);
        user.setInfo(info);
        return user;
    }
}
