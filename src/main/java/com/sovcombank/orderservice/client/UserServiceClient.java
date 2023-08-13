package com.sovcombank.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@FeignClient("user")
public interface UserServiceClient {
    @GetMapping("/users")
    Set<String> getOptimalHRBPs(@RequestParam("count") int count);
}
