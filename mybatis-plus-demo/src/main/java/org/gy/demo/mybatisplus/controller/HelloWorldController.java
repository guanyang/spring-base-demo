package org.gy.demo.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import java.time.LocalDateTime;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.mybatisplus.entity.HelloWorld;
import org.gy.demo.mybatisplus.service.IHelloWorldService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2021/11/29 13:32
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class HelloWorldController {

    @Resource
    private IHelloWorldService helloWorldService;

    @GetMapping("/list")
    public ResponseEntity list(PageDTO page) {
        LambdaQueryWrapper<HelloWorld> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HelloWorld::getIsDeleted, 0);
        page = helloWorldService.page(page, wrapper);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/add")
    public ResponseEntity add(HelloWorld world) {
        world.setGmtCreated(LocalDateTime.now());
        world.setGmtModified(LocalDateTime.now());
        helloWorldService.save(world);
        return ResponseEntity.ok(world);
    }
}
