package com.meditrack.payment.controller;

import com.meditrack.common.dto.ApiResponse;
import com.meditrack.payment.dto.CreatePaymentRequest;
import com.meditrack.payment.dto.PaymentDTO;
import com.meditrack.payment.model.PaymentStatus;
import com.meditrack.payment.model.PaymentType;
import com.meditrack.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<PaymentDTO>> createPayment(@RequestBody CreatePaymentRequest request) {
        PaymentDTO payment = paymentService.createPayment(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment created successfully", payment));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentById(@PathVariable Long id) {
        PaymentDTO payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment retrieved successfully", payment));
    }
    
    @GetMapping("/number/{paymentNumber}")
    public ResponseEntity<ApiResponse<PaymentDTO>> getPaymentByNumber(@PathVariable String paymentNumber) {
        PaymentDTO payment = paymentService.getPaymentByNumber(paymentNumber);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment retrieved successfully", payment));
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getPaymentsByPatient(@PathVariable Long patientId) {
        List<PaymentDTO> payments = paymentService.getPaymentsByPatient(patientId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payments retrieved successfully", payments));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        List<PaymentDTO> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payments retrieved successfully", payments));
    }

    
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<PaymentDTO>>> getPaymentsByType(@PathVariable PaymentType type) {
        List<PaymentDTO> payments = paymentService.getPaymentsByType(type);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payments retrieved successfully", payments));
    }
    
    @PostMapping("/{id}/process")
    public ResponseEntity<ApiResponse<PaymentDTO>> processPayment(@PathVariable Long id) {
        PaymentDTO payment = paymentService.processPayment(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment processed", payment));
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PaymentDTO>> updatePaymentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        PaymentStatus status = PaymentStatus.valueOf(request.get("status"));
        String notes = request.get("notes");
        PaymentDTO payment = paymentService.updatePaymentStatus(id, status, notes);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment status updated successfully", payment));
    }
    
    @PostMapping("/{id}/refund")
    public ResponseEntity<ApiResponse<PaymentDTO>> refundPayment(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String reason = request.get("reason");
        PaymentDTO payment = paymentService.refundPayment(id, reason);
        return ResponseEntity.ok(new ApiResponse<>(true, "Payment refunded successfully", payment));
    }
}
