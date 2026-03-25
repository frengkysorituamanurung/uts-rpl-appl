package com.meditrack.ehr.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateLabResultRequest {
    private Long patientId;
    private Long doctorId;
    private Long medicalRecordId;
    private String testName;
    private String testType;
    private LocalDate testDate;
    private LocalDate resultDate;
    private String result;
    private String normalRange;
    private String unit;
    private String labName;
    private String technician;
    private String notes;
}
