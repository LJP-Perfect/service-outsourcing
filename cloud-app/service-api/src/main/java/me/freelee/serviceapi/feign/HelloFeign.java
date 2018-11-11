package me.freelee.serviceapi.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient("service-provider")
public interface HelloFeign {

    @GetMapping("/hello")
    public String hello();
}
