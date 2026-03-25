package com.meditrack.ehr.dto;

import com.meditrack.ehr.model.ResultStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LabResultDTO {
    private Long id;
    private String resultNumber;
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
    private ResultStatus status;
    private String labName;
    private String technician;
    private String notes;
    private LocalDateTime createdAt;
}
