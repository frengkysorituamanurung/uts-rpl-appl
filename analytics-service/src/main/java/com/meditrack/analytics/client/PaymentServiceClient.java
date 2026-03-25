package com.meditrack.analytics.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "payment-service")
public interface PaymentServiceClient {
    
    @GetMapping("/api/payments/status/{status}")
    Map<String, Object> getPaymentsByStatus(@PathVariable String status);
    
    @GetMapping("/api/payments/type/{type}")
    Map<String, Object> getPaymentsByType(@PathVariable String type);
}
