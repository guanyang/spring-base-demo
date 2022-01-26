package org.gy.demo.gemfire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.geode.boot.autoconfigure.PdxSerializationAutoConfiguration;

@SpringBootApplication(exclude = PdxSerializationAutoConfiguration.class)
public class GemfireDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GemfireDemoApplication.class, args);
    }

}
