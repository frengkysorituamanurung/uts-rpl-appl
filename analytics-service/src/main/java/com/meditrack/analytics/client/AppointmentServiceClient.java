package com.meditrack.analytics.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(name = "appointment-service")
public interface AppointmentServiceClient {
    
    @GetMapping("/api/appointments/status/{status}")
    Map<String, Object> getAppointmentsByStatus(@PathVariable String status);
}
