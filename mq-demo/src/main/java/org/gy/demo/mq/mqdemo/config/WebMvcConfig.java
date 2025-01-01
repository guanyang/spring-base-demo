package org.gy.demo.mq.mqdemo.config;

import org.gy.demo.mq.mqdemo.trace.TraceEnum;
import org.gy.framework.core.trace.TraceUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @author gy
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer, InitializingBean {

//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//            .allowedOrigins("*")
//            .allowedMethods("POST", "GET", "OPTIONS", "DELETE", "PATCH")
//            .allowedHeaders("*")
//            .allowCredentials(false).maxAge(3600);
//    }

//    @Bean
//    public FilterRegistrationBean<TraceFilter> traceFilterRegistration() {
//        FilterRegistrationBean<TraceFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new TraceFilter());
//        registrationBean.addUrlPatterns("/*");
//        registrationBean.setName("traceFilter");
//        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        return registrationBean;
//    }

    @Override
    public void afterPropertiesSet() throws Exception {
        TraceUtils.setLogTraceKey(TraceEnum.TRACE.getTraceName());
    }
}
