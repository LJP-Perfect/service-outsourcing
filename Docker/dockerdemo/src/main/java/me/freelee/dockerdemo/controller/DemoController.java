package me.freelee.dockerdemo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Description:
 * Date:2018/10/27
 *
 * @author:Lee
 */
@RestController
public class DemoController {

    @RequestMapping("/hello")
    public String hello(){
        return "hello world docker";
    }
}
