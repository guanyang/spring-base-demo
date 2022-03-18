package org.gy.demo.femas.provider.controller;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 功能描述：
 *
 * @author gy
 * @version 1.0.0
 */
@FeignClient(name = "femas-provider")
public interface ProviderService {

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    String hello();

    @RequestMapping(value = "/echo/{str}", method = RequestMethod.GET)
    String echo(@PathVariable("str") String str);

    @RequestMapping(value = "/echo/error/{str}", method = RequestMethod.GET)
    String echoError(@PathVariable("str") String str);

    @RequestMapping(value = "/echo/slow/{str}", method = RequestMethod.GET)
    String echoSlow(@PathVariable("str") String str);
}