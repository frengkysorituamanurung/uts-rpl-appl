package com.meditrack.payment.service;

import com.meditrack.payment.dto.CreatePaymentRequest;
import com.meditrack.payment.dto.PaymentDTO;
import com.meditrack.payment.model.Payment;
import com.meditrack.payment.model.PaymentStatus;
import com.meditrack.payment.model.PaymentType;
import com.meditrack.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Transactional
    public PaymentDTO createPayment(CreatePaymentRequest request) {
        // Check if payment already exists for this reference
        paymentRepository.findByReferenceIdAndPaymentType(request.getReferenceId(), request.getPaymentType())
                .ifPresent(payment -> {
                    throw new RuntimeException("Payment already exists for this " + request.getPaymentType());
                });
        
        Payment payment = new Payment();
        payment.setPaymentNumber(generatePaymentNumber());
        payment.setPatientId(request.getPatientId());
        payment.setPaymentType(request.getPaymentType());
        payment.setReferenceId(request.getReferenceId());
        payment.setReferenceNumber(request.getReferenceNumber());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setDescription(request.getDescription());
        payment.setNotes(request.getNotes());
        payment.setStatus(PaymentStatus.PENDING);
        
        Payment saved = paymentRepository.save(payment);
        
        System.out.println("Payment created: " + saved.getPaymentNumber() + " for " + saved.getPaymentType());
        
        return convertToDTO(saved);
    }

    
    public PaymentDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return convertToDTO(payment);
    }
    
    public PaymentDTO getPaymentByNumber(String paymentNumber) {
        Payment payment = paymentRepository.findByPaymentNumber(paymentNumber)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return convertToDTO(payment);
    }
    
    public List<PaymentDTO> getPaymentsByPatient(Long patientId) {
        return paymentRepository.findByPatientId(patientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<PaymentDTO> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<PaymentDTO> getPaymentsByType(PaymentType type) {
        return paymentRepository.findByPaymentType(type).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public PaymentDTO processPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new RuntimeException("Payment is not in PENDING status");
        }
        
        payment.setStatus(PaymentStatus.PROCESSING);
        
        // Simulate payment processing
        try {
            // In real implementation, this would call payment gateway
            Thread.sleep(1000);
            
            // Simulate success (90% success rate)
            boolean success = Math.random() < 0.9;
            
            if (success) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setPaidAt(LocalDateTime.now());
                payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
                System.out.println("Payment processed successfully: " + payment.getPaymentNumber());
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason("Payment gateway declined the transaction");
                System.out.println("Payment failed: " + payment.getPaymentNumber());
            }
        } catch (InterruptedException e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Payment processing interrupted");
        }
        
        Payment updated = paymentRepository.save(payment);
        return convertToDTO(updated);
    }
    
    @Transactional
    public PaymentDTO updatePaymentStatus(Long id, PaymentStatus newStatus, String notes) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        // Validate status transitions
        if (payment.getStatus() == PaymentStatus.COMPLETED && newStatus != PaymentStatus.REFUNDED) {
            throw new RuntimeException("Completed payment can only be refunded");
        }
        
        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new RuntimeException("Cannot update status of refunded payment");
        }
        
        payment.setStatus(newStatus);
        
        if (newStatus == PaymentStatus.COMPLETED && payment.getPaidAt() == null) {
            payment.setPaidAt(LocalDateTime.now());
        }
        
        if (notes != null) {
            payment.setNotes(notes);
        }
        
        Payment updated = paymentRepository.save(payment);
        return convertToDTO(updated);
    }
    
    @Transactional
    public PaymentDTO refundPayment(Long id, String reason) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new RuntimeException("Only completed payments can be refunded");
        }
        
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setNotes("Refunded: " + reason);
        
        Payment updated = paymentRepository.save(payment);
        
        System.out.println("Payment refunded: " + payment.getPaymentNumber());
        
        return convertToDTO(updated);
    }
    
    private String generatePaymentNumber() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setPaymentNumber(payment.getPaymentNumber());
        dto.setPatientId(payment.getPatientId());
        dto.setPaymentType(payment.getPaymentType());
        dto.setReferenceId(payment.getReferenceId());
        dto.setReferenceNumber(payment.getReferenceNumber());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setStatus(payment.getStatus());
        dto.setDescription(payment.getDescription());
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaidAt(payment.getPaidAt());
        dto.setNotes(payment.getNotes());
        dto.setFailureReason(payment.getFailureReason());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }
}
