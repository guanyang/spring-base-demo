package org.gy.demo.log;

import cn.hutool.core.io.IoUtil;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import org.assertj.core.util.Lists;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/8/8 15:57
 */
public class LogFileTest {

    public static void main(String[] args) throws IOException {
        List<String> result = Lists.newArrayList();
        String fileName = "/Users/gy/Downloads/Log01.log";
        try (FileInputStream is = new FileInputStream(fileName)) {
            IoUtil.readUtf8Lines(is, result);
        }
        System.out.println(result.size());
    }

}
