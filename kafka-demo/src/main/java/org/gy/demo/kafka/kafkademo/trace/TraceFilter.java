package org.gy.demo.kafka.kafkademo.trace;

import cn.hutool.core.util.StrUtil;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author gy
 */
public class TraceFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String traceId = httpServletRequest.getHeader(TraceEnum.TRACE.getName());
        if (StrUtil.isNotBlank(traceId)) {
            TraceContext.setTrace(traceId);
        } else {
            TraceContext.getTrace();
        }
        try {
            filterChain.doFilter(httpServletRequest, servletResponse);
        } finally {
            TraceContext.clearTrace();
        }
    }
}
