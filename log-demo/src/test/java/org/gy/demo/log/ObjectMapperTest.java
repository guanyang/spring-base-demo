package org.gy.demo.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 * @date 2022/8/2 17:03
 */
public class ObjectMapperTest {

    public static void main(String[] args) throws JsonProcessingException {
        DataItem objData = new DataItem();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(objData));
    }

    @Data
    public static class DataItem {

        private int cType;
        private int clType;
    }

}
