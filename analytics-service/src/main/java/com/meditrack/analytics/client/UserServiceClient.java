package com.meditrack.analytics.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "user-service")
public interface UserServiceClient {
    
    @GetMapping("/api/users/role/{role}")
    Map<String, Object> getUsersByRole(@PathVariable String role);
}
