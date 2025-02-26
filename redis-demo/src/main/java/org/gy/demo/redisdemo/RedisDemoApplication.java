package org.gy.demo.redisdemo;

import org.gy.framework.idempotent.annotation.EnableIdempotent;
import org.gy.framework.limit.annotation.EnableLimitCheck;
import org.gy.framework.lock.annotation.EnableLockAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableLimitCheck
@EnableIdempotent
@EnableLockAspect
public class RedisDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisDemoApplication.class, args);
    }

}
