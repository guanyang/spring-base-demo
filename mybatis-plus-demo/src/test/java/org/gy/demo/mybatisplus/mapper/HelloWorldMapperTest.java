package org.gy.demo.mybatisplus.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Resource;
import org.gy.demo.mybatisplus.entity.HelloWorldNew;
import org.gy.demo.mybatisplus.service.IHelloWorldNewService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HelloWorldMapperTest {

    @Resource
    private HelloWorldNewMapper helloWorldNewMapper;

    @Resource
    private IHelloWorldNewService helloWorldNewService;

    @Test
    void insertBatchSomeColumnTest() {
        //实现批量插入
        List<HelloWorldNew> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            HelloWorldNew entity = new HelloWorldNew();
            entity.setName("gy-" + UUID.randomUUID());
            entity.setCreateBy("gy");
            entity.setCreateTime(LocalDateTime.now());
//            entity.setVersion(0);
            entity.setUpdateBy("gy");
            entity.setUpdateTime(LocalDateTime.now());
//            entity.setDeleted(0);
            list.add(entity);
        }
        boolean result = helloWorldNewService.insertBatchSomeColumn(list,2);
        Assertions.assertTrue(result);
    }

    @Test
    void updateBatchByIdTest() {
        //实现批量更新
        List<HelloWorldNew> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            HelloWorldNew entity = new HelloWorldNew();
            entity.setId((long) (i + 1));
            entity.setName("gy-" + UUID.randomUUID());
//            entity.setCreateBy("gy");
//            entity.setCreateTime(LocalDateTime.now());
//            entity.setVersion(0);
            entity.setUpdateBy("gy");
            entity.setUpdateTime(LocalDateTime.now());
//            entity.setDeleted(0);
            list.add(entity);
        }
        boolean result = helloWorldNewService.updateBatchById(list);
        Assertions.assertTrue(result);
    }
}