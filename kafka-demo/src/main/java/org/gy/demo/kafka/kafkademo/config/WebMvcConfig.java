package org.gy.demo.kafka.kafkademo.config;

import org.gy.demo.kafka.kafkademo.trace.TraceFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @author gy
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

//    @Bean
//    public FilterRegistrationBean<TraceFilter> traceFilterRegistration() {
//        FilterRegistrationBean<TraceFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new TraceFilter());
//        registrationBean.addUrlPatterns("/*");
//        registrationBean.setName("traceFilter");
//        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        return registrationBean;
//    }
}
