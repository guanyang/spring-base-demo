package org.gy.demo.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.gy.demo.mybatisplus.entity.HelloWorldNew;
import org.gy.demo.mybatisplus.service.IHelloWorldNewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 新演示表 前端控制器
 * </p>
 *
 * @author gy
 * @since 2023-07-14
 */
@RestController
@RequestMapping("/api/v2")
public class HelloWorldNewController {

    @Resource
    private IHelloWorldNewService helloWorldNewService;

    @GetMapping("/list")
    public ResponseEntity<PageDTO<HelloWorldNew>> list(PageDTO<HelloWorldNew> page) {
        helloWorldNewService.lambdaQuery().page(page);
        return ResponseEntity.ok(page);
    }

    @PostMapping("/add")
    public ResponseEntity<Boolean> add(HelloWorldNew world) {
        return ResponseEntity.ok(helloWorldNewService.save(world));
    }

    @PostMapping("/update")
    public ResponseEntity<Boolean> update(HelloWorldNew world) {
        return ResponseEntity.ok(helloWorldNewService.updateById(world));
    }

    @PostMapping("/delete")
    public ResponseEntity<Boolean> delete(Long id) {
        return ResponseEntity.ok(helloWorldNewService.removeById(id));
    }

    @PostMapping("/deleteAll")
    public ResponseEntity<Boolean> deleteAll(String name) {
        LambdaQueryWrapper<HelloWorldNew> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(HelloWorldNew::getName, name);
        return ResponseEntity.ok(helloWorldNewService.remove(wrapper));
    }

}
