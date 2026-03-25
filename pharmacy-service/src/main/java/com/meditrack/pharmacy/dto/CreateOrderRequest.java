package com.meditrack.pharmacy.dto;

import java.util.List;

public class CreateOrderRequest {
    private String prescriptionNumber;
    private Long patientId;
    private Long pharmacistId;
    private List<OrderItemRequest> items;
    private String notes;
    
    // Getters and Setters
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
    
    public List<OrderItemRequest> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
