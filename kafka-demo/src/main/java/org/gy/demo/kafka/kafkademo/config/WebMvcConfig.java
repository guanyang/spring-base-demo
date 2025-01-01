package org.gy.demo.kafka.kafkademo.config;

import org.gy.demo.kafka.kafkademo.trace.TraceEnum;
import org.gy.framework.core.trace.TraceUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @author gy
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer, InitializingBean {

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
