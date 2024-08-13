package org.gy.demo.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.gy.demo.mybatisplus.MybatisPlusDemoApplication;
import org.gy.demo.mybatisplus.entity.HelloWorldNew;
import org.gy.demo.mybatisplus.service.IHelloWorldNewService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@SpringBootTest(classes = MybatisPlusDemoApplication.class)
class HelloWorldMapperTest {

    @Resource
    private HelloWorldNewMapper helloWorldNewMapper;

    @Resource
    private IHelloWorldNewService helloWorldNewService;

    @Test
    void updateBatchByWrapperTest() {
        List<HelloWorldNew> list = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            HelloWorldNew entity = new HelloWorldNew();
//            entity.setName("gy-" + UUID.randomUUID());
            entity.setUpdateBy("gy");
            entity.setUpdateTime(LocalDateTime.now());
            list.add(entity);
        }
        Function<HelloWorldNew, LambdaQueryWrapper<HelloWorldNew>> wrapperFunction = (entity) -> {
            return new LambdaQueryWrapper<HelloWorldNew>().eq(HelloWorldNew::getDeleted, 0).eq(HelloWorldNew::getVersion, 0);
        };
        boolean result = helloWorldNewService.updateBatchByWrapper(list, wrapperFunction);
        Assertions.assertTrue(result);
    }

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
        boolean result = helloWorldNewService.insertBatchSomeColumn(list, 2);
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

    @Test
    void transactionTest1() throws InterruptedException {
        try {
            helloWorldNewService.test10();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 若是单测 请务必持有住主线程  否则子线程被提前结束不生效
        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    void transactionTest2() throws InterruptedException {
        try {
            helloWorldNewService.test20();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 若是单测 请务必持有住主线程  否则子线程被提前结束不生效
        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    void transactionTest3() throws InterruptedException {
        try {
            helloWorldNewService.test30();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 若是单测 请务必持有住主线程  否则子线程被提前结束不生效
        TimeUnit.SECONDS.sleep(3);
    }

    @Test
    void transactionTest4() throws InterruptedException {
        try {
            helloWorldNewService.test40();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 若是单测 请务必持有住主线程  否则子线程被提前结束不生效
        TimeUnit.SECONDS.sleep(3);
    }
}