package com.meditrack.ehr.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "medical_records")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long doctorId;

    private Long appointmentId;

    @Column(nullable = false)
    private LocalDateTime visitDate;

    private String chiefComplaint;
    
    @Column(length = 1000)
    private String diagnosis;
    
    @Column(length = 2000)
    private String treatmentPlan;
    
    @Column(length = 2000)
    private String notes;

    // Vital Signs
    private Double temperature; // Celsius
    private Integer bloodPressureSystolic;
    private Integer bloodPressureDiastolic;
    private Integer heartRate;
    private Integer respiratoryRate;
    private Double weight; // kg
    private Double height; // cm
    private Integer oxygenSaturation;

    @Enumerated(EnumType.STRING)
    private RecordStatus status = RecordStatus.DRAFT;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
}
