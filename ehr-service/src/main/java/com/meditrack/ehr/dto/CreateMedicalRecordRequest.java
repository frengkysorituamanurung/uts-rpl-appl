package com.meditrack.ehr.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateMedicalRecordRequest {
    private Long patientId;
    private Long doctorId;
    private Long appointmentId;
    private LocalDateTime visitDate;
    private String chiefComplaint;
    private String diagnosis;
    private String treatmentPlan;
    private String notes;
    
    // Vital Signs
    private Double temperature;
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private Integer heartRate;
    private Integer respiratoryRate;
    private Double weight;
    private Double height;
    private Integer oxygenSaturation;
}
