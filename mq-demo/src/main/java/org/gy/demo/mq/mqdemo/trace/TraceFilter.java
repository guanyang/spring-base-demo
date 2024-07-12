package org.gy.demo.mq.mqdemo.trace;

import cn.hutool.core.util.StrUtil;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.filter.GenericFilterBean;

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
