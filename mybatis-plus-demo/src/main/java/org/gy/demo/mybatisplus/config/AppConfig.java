package org.gy.demo.mybatisplus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
public class AppConfig {

    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        // 设置快速失败模式
        validator.getValidationPropertyMap().put("hibernate.validator.fail_fast", "true");
        return validator;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(LocalValidatorFactoryBean validatorFactoryBean) {
        MethodValidationPostProcessor validationPostProcessor = new MethodValidationPostProcessor();
        validationPostProcessor.setValidator(validatorFactoryBean);
        return validationPostProcessor;
    }
}
