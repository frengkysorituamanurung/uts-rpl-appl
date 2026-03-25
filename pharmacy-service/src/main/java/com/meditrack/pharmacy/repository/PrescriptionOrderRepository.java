package com.meditrack.pharmacy.repository;

import com.meditrack.pharmacy.model.OrderStatus;
import com.meditrack.pharmacy.model.PrescriptionOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrescriptionOrderRepository extends JpaRepository<PrescriptionOrder, Long> {
    
    Optional<PrescriptionOrder> findByOrderNumber(String orderNumber);
    
    Optional<PrescriptionOrder> findByPrescriptionNumber(String prescriptionNumber);
    
    List<PrescriptionOrder> findByPatientId(Long patientId);
    
    List<PrescriptionOrder> findByPharmacistId(Long pharmacistId);
    
    List<PrescriptionOrder> findByStatus(OrderStatus status);
}
