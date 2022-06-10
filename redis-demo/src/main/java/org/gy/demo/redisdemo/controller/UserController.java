package org.gy.demo.redisdemo.controller;

import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.redisdemo.model.User;
import org.gy.demo.redisdemo.service.UserService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserService userService;

    @GetMapping("/get/{id}")
    public User getUser(@PathVariable("id") Integer id) {
        return userService.get(id);
    }

    @GetMapping("/getByName/{name}")
    public User getByName(@PathVariable("name") String name) {
        return userService.getByName(name);
    }

    @PostMapping("/update")
    public User updateUser(@RequestBody User user) {
        return userService.save(user);
    }

    @DeleteMapping("/delete/{id}")
    public boolean deleteUser(@PathVariable("id") Integer id) {
        userService.delete(id);
        return true;
    }

}
