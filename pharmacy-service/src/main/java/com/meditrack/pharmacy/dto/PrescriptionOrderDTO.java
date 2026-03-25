package com.meditrack.pharmacy.dto;

import com.meditrack.pharmacy.model.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PrescriptionOrderDTO {
    private Long id;
    private String orderNumber;
    private String prescriptionNumber;
    private Long patientId;
    private Long pharmacistId;
    private List<OrderItemDTO> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime orderedAt;
    private LocalDateTime dispensedAt;
    private String notes;
    private String pharmacistNotes;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOrderNumber() {
        return orderNumber;
    }
    
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    public String getPrescriptionNumber() {
        return prescriptionNumber;
    }
    
    public void setPrescriptionNumber(String prescriptionNumber) {
        this.prescriptionNumber = prescriptionNumber;
    }
    
    public Long getPatientId() {
        return patientId;
    }
    
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }
    
    public Long getPharmacistId() {
        return pharmacistId;
    }
    
    public void setPharmacistId(Long pharmacistId) {
        this.pharmacistId = pharmacistId;
    }
    
    public List<OrderItemDTO> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getOrderedAt() {
        return orderedAt;
    }
    
    public void setOrderedAt(LocalDateTime orderedAt) {
        this.orderedAt = orderedAt;
    }
    
    public LocalDateTime getDispensedAt() {
        return dispensedAt;
    }
    
    public void setDispensedAt(LocalDateTime dispensedAt) {
        this.dispensedAt = dispensedAt;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getPharmacistNotes() {
        return pharmacistNotes;
    }
    
    public void setPharmacistNotes(String pharmacistNotes) {
        this.pharmacistNotes = pharmacistNotes;
    }
}
