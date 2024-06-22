package org.gy.demo.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.zaxxer.hikari.HikariConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.annotation.Resource;
import org.gy.demo.mybatisplus.entity.HelloWorldNew;
import org.gy.demo.mybatisplus.service.IHelloWorldNewService;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/versionTest")
    public ResponseEntity<Map<String, Boolean>> versionTest(Long id) {
        //模拟线程1更新数据
        HelloWorldNew entity1 = helloWorldNewService.getById(id);
        entity1.setName("test123");

        //模拟线程2更新数据
        HelloWorldNew entity2 = helloWorldNewService.getById(id);
        entity2.setName("test456");

        boolean b2 = helloWorldNewService.updateById(entity2);

        boolean b1 = helloWorldNewService.updateById(entity1);
        Map<String, Boolean> result = new HashMap<>();
        result.put("b1", b1);
        result.put("b2", b2);
        return ResponseEntity.ok(result);
    }

    public static void main(String[] args) {
        YamlPropertiesFactoryBean factoryBean = new YamlPropertiesFactoryBean();
//        factoryBean.setResources(HelloWorldNewController.class.getClassLoader().getResource("application.yml"));
        factoryBean.setResources(new ClassPathResource("rule-config-dev.yaml"));
        Properties object = factoryBean.getObject();
        HikariConfig config = new HikariConfig(object);
        System.out.println(config);

    }

}
