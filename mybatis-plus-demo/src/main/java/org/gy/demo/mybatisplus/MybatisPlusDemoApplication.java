package org.gy.demo.mybatisplus;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import lombok.extern.slf4j.Slf4j;
import org.gy.demo.mybatisplus.trace.TraceContext;
import org.gy.framework.mq.annotation.EnableMQ;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.time.ZoneId;

@Slf4j
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
@EnableMQ
public class MybatisPlusDemoApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(MybatisPlusDemoApplication.class, args);
            log.info("Timezone: {}", ZoneId.systemDefault());
            log.info("MybatisPlusDemoApplication -> start! TraceId: {}", TraceContext.getTraceId());
        } finally {
            TraceContext.clearTrace();
        }
    }

}
