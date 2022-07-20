package org.gy.demo.redisdemo;

import java.lang.reflect.Method;
import org.springframework.util.ReflectionUtils;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/7/5 21:15
 */
public class Test {

    public static void main(String[] args) {
        Method method = ReflectionUtils.findMethod(String.class, "valueOf", Object.class);
        Object o = ReflectionUtils.invokeMethod(method, null,10);
        System.out.println(o);
    }

}
