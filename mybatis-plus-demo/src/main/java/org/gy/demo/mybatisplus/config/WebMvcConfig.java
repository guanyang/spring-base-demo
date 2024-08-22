package org.gy.demo.mybatisplus.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author gy
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private LocalValidatorFactoryBean validatorFactoryBean;

    @Override
    public Validator getValidator() {
        return validatorFactoryBean;
    }
}
