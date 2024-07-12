package org.gy.demo.mq.mqdemo.config;

import org.gy.demo.mq.mqdemo.trace.TraceFilter;
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

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//            .allowedOrigins("*")
//            .allowedMethods("POST", "GET", "OPTIONS", "DELETE", "PATCH")
//            .allowedHeaders("*")
//            .allowCredentials(false).maxAge(3600);
//    }

    @Bean
    public FilterRegistrationBean<TraceFilter> traceFilterRegistration() {
        FilterRegistrationBean<TraceFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TraceFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("traceFilter");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}
