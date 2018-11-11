package me.freelee.serviceapi.controller;

import me.freelee.serviceapi.feign.HelloFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description:
 * Date:2018/11/10
 *
 * @author:Lee
 */
@RestController
public class HelloContrller {

    @Autowired
    HelloFeign helloFeign;

    @GetMapping("/hello")
    public String hello(){
        return helloFeign.hello();
    }
}
