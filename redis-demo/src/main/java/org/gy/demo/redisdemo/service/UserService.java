package org.gy.demo.redisdemo.service;

import org.gy.demo.redisdemo.model.User;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 */
public interface UserService {

    User get(Integer id);

    User save(User user);

    void delete(Integer id);

    User getByName(String name);

}
