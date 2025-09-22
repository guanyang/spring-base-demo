package org.gy.demo.mybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.mybatisplus.entity.HelloWorldNew;
import org.gy.demo.mybatisplus.model.TestRequest;
import org.gy.demo.mybatisplus.model.TestResponse;
import org.gy.demo.mybatisplus.service.IHelloWorldNewService;
import org.gy.framework.mq.core.EventMessageProducerService;
import org.gy.framework.mq.core.support.EventMessageServiceManager;
import org.gy.framework.mq.model.IEventType.DefaultEventType;
import org.gy.framework.mq.model.IMessageType.MessageTypeCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 新演示表 前端控制器
 * </p>
 *
 * @author gy
 * @since 2023-07-14
 */
@Slf4j
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

    @GetMapping("/testValid")
    public ResponseEntity<TestResponse> hello(TestRequest request) {
        return ResponseEntity.ok(helloWorldNewService.hello(request));
    }

    @GetMapping("/sendMsg")
    public ResponseEntity<TestRequest> sendMsg(TestRequest request) {
        EventMessageProducerService sendService = EventMessageServiceManager.getSendService(MessageTypeCode.DEFAULT_KAFKA);
        sendService.asyncSend(request, DefaultEventType.DEMO_EVENT);
        log.info("[sendMsg]发送消息成功：{}", request);
        return ResponseEntity.ok(request);
    }

}
