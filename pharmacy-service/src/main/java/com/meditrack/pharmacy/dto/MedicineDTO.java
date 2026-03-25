package com.meditrack.pharmacy.dto;

import com.meditrack.pharmacy.model.MedicineCategory;
import com.meditrack.pharmacy.model.MedicineStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MedicineDTO {
    private Long id;
    private String medicineCode;
    private String name;
    private String description;
    private String manufacturer;
    private MedicineCategory category;
    private String dosageForm;
    private String strength;
    private BigDecimal price;
    private Integer stockQuantity;
    private Integer reorderLevel;
    private LocalDate expiryDate;
    private Boolean requiresPrescription;
    private MedicineStatus status;
    private String storageConditions;
    private String sideEffects;
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getMedicineCode() {
        return medicineCode;
    }
    
    public void setMedicineCode(String medicineCode) {
        this.medicineCode = medicineCode;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getManufacturer() {
        return manufacturer;
    }
    
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    
    public MedicineCategory getCategory() {
        return category;
    }
    
    public void setCategory(MedicineCategory category) {
        this.category = category;
    }
    
    public String getDosageForm() {
        return dosageForm;
    }
    
    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }
    
    public String getStrength() {
        return strength;
    }
    
    public void setStrength(String strength) {
        this.strength = strength;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public Integer getReorderLevel() {
        return reorderLevel;
    }
    
    public void setReorderLevel(Integer reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
    
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public Boolean getRequiresPrescription() {
        return requiresPrescription;
    }
    
    public void setRequiresPrescription(Boolean requiresPrescription) {
        this.requiresPrescription = requiresPrescription;
    }
    
    public MedicineStatus getStatus() {
        return status;
    }
    
    public void setStatus(MedicineStatus status) {
        this.status = status;
    }
    
    public String getStorageConditions() {
        return storageConditions;
    }
    
    public void setStorageConditions(String storageConditions) {
        this.storageConditions = storageConditions;
    }
    
    public String getSideEffects() {
        return sideEffects;
    }
    
    public void setSideEffects(String sideEffects) {
        this.sideEffects = sideEffects;
    }
}
