package com.meditrack.analytics.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@FeignClient(name = "pharmacy-service")
public interface PharmacyServiceClient {
    
    @GetMapping("/api/pharmacy/medicines/low-stock")
    Map<String, Object> getLowStockMedicines();
    
    @GetMapping("/api/pharmacy/medicines")
    Map<String, Object> getAllMedicines();
}
