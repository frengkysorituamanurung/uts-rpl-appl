package com.meditrack.pharmacy.dto;

public class UpdateStockRequest {
    private Integer quantity;
    private String operation; // ADD or SUBTRACT
    
    // Getters and Setters
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
}
