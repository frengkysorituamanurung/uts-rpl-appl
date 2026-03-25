package com.meditrack.payment.repository;

import com.meditrack.payment.model.Payment;
import com.meditrack.payment.model.PaymentStatus;
import com.meditrack.payment.model.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    Optional<Payment> findByPaymentNumber(String paymentNumber);
    
    List<Payment> findByPatientId(Long patientId);
    
    List<Payment> findByStatus(PaymentStatus status);
    
    List<Payment> findByPaymentType(PaymentType paymentType);
    
    Optional<Payment> findByReferenceIdAndPaymentType(Long referenceId, PaymentType paymentType);
    
    List<Payment> findByPatientIdAndStatus(Long patientId, PaymentStatus status);
}
