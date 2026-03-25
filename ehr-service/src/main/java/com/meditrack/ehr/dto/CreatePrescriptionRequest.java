package com.meditrack.ehr.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreatePrescriptionRequest {
    private Long patientId;
    private Long doctorId;
    private Long medicalRecordId;
    private LocalDate issueDate;
    private Integer validityDays = 30;
    private String notes;
    private List<PrescriptionItemDTO> items;
}
