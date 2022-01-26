package org.gy.demo.testmockdemo.utils;


import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2021/11/8 16:55
 */
@SpringBootTest
public class RequestUtilsTests {

    @Test
    public void headerValue() {
        String source = "hello";
        //mock参数和行为
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(Mockito.anyString())).thenReturn(source);

        //方法调用
        String target = RequestUtils.headerValue(request, Mockito.anyString());

        //验证执行次数
        Mockito.verify(request).getHeader(Mockito.anyString());

        //断言执行结果
        Assert.assertEquals(source, target);

    }

    @Test
    public void parameterValue() {

        String source = "hello";
        //mock参数和行为
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getParameter(Mockito.anyString())).thenReturn(source);

        //方法调用
        String target = RequestUtils.parameterValue(request, Mockito.anyString());

        //验证执行次数
        Mockito.verify(request).getParameter(Mockito.anyString());

        //断言执行结果
        Assert.assertEquals(source, target);
    }

    @Test
    public void referer() {
        String source = "hello";
        //mock参数和行为
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getHeader(Mockito.anyString())).thenReturn(source);

        //方法调用
        String target = RequestUtils.referer(request);

        //验证执行次数
        Mockito.verify(request).getHeader(Mockito.anyString());

        //断言执行结果
        Assert.assertEquals(source, target);

    }
}