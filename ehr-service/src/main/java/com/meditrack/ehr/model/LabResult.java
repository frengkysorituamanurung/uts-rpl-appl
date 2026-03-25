package com.meditrack.ehr.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "lab_results")
public class LabResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String resultNumber;

    @Column(nullable = false)
    private Long patientId;

    @Column(nullable = false)
    private Long doctorId;

    private Long medicalRecordId;

    @Column(nullable = false)
    private String testName;

    private String testType;

    @Column(nullable = false)
    private LocalDate testDate;

    private LocalDate resultDate;

    @Column(length = 2000)
    private String result;

    private String normalRange;
    private String unit;

    @Enumerated(EnumType.STRING)
    private ResultStatus status = ResultStatus.PENDING;

    private String labName;
    private String technician;
    
    @Column(length = 1000)
    private String notes;

    private LocalDateTime createdAt = LocalDateTime.now();
}
