package me.freelee.serviceapi.feign;

import org.springframework.stereotype.Component;

/**
 * Description:
 * Date:2018/11/10
 *
 * @author:Lee
 */
@Component
public class HelloFallback implements HelloFeign{
    @Override
    public String hello() {
        return "error";
    }
}
