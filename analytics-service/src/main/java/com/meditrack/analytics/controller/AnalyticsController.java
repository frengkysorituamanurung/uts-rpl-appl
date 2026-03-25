package com.meditrack.analytics.controller;

import com.meditrack.analytics.dto.DashboardSummaryDTO;
import com.meditrack.analytics.service.AnalyticsService;
import com.meditrack.common.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardSummaryDTO>> getDashboardSummary() {
        DashboardSummaryDTO summary = analyticsService.getDashboardSummary();
        return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard summary retrieved successfully", summary));
    }
    
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<DashboardSummaryDTO.UserStatistics>> getUserStatistics() {
        DashboardSummaryDTO.UserStatistics stats = analyticsService.getUserStatistics();
        return ResponseEntity.ok(new ApiResponse<>(true, "User statistics retrieved successfully", stats));
    }
    
    @GetMapping("/appointments")
    public ResponseEntity<ApiResponse<DashboardSummaryDTO.AppointmentStatistics>> getAppointmentStatistics() {
        DashboardSummaryDTO.AppointmentStatistics stats = analyticsService.getAppointmentStatistics();
        return ResponseEntity.ok(new ApiResponse<>(true, "Appointment statistics retrieved successfully", stats));
    }
    
    @GetMapping("/payments")
    public ResponseEntity<ApiResponse<DashboardSummaryDTO.PaymentStatistics>> getPaymentStatistics() {
        DashboardSummaryDTO.PaymentStatistics stats = analyticsService.getPaymentStatistics();
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment statistics retrieved successfully", stats));
    }
    
    @GetMapping("/pharmacy")
    public ResponseEntity<ApiResponse<DashboardSummaryDTO.PharmacyStatistics>> getPharmacyStatistics() {
        DashboardSummaryDTO.PharmacyStatistics stats = analyticsService.getPharmacyStatistics();
        return ResponseEntity.ok(new ApiResponse<>(true, "Pharmacy statistics retrieved successfully", stats));
    }
}
