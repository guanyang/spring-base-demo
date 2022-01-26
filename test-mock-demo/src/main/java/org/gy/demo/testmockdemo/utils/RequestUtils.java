package org.gy.demo.testmockdemo.utils;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Lixiaochun
 */
@Slf4j
public class RequestUtils {

    private static final String HN_REFERER = "referer";

    public static String headerValue(HttpServletRequest request, String name) {
        return request.getHeader(name);
    }

    public static String parameterValue(HttpServletRequest request, String name) {
        return request.getParameter(name);
    }

    public static String referer(HttpServletRequest request) {
        return request.getHeader(HN_REFERER);
    }

}
