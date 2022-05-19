///*
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package org.gy.demo.webflux.controller;
//
//import com.google.common.collect.Maps;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.Map;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.io.IOUtils;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Mono;
//
///**
// * TestController.
// */
//@Slf4j
//@RestController
//@RequestMapping("/test")
//public class HttpTestController {
//
//    private static final String DEFAULT_FILE = "/data/apiTest/tan.response";
//
//    private static byte[] ADX_TAN;
//
//    static {
//        try {
//            ADX_TAN = IOUtils.toByteArray(new FileInputStream(DEFAULT_FILE));
//        } catch (IOException e) {
//            log.error("load adx tan exception:file={}.", DEFAULT_FILE, e);
//        }
//    }
//
//    /**
//     * Find by user id string.
//     *
//     * @param userId the user id
//     * @return the string
//     */
//    @GetMapping("/findByUserId")
//    public Mono<Map> findByUserId(@RequestParam("userId") final String userId) {
//        Map<String, Object> map = Maps.newHashMap();
//        map.put("userId", userId);
//        map.put("userName", "hello world");
//        return Mono.just(map);
//    }
//
//    @PostMapping(value = "/adx/tan", produces = "application/octet-stream")
//    public Mono<byte[]> adx(int init) throws IOException {
//        if (init == 1) {
//            ADX_TAN = IOUtils.toByteArray(new FileInputStream(DEFAULT_FILE));
//        }
//        return Mono.just(ADX_TAN);
//    }
//
//
//}
