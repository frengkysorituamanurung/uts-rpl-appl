package com.meditrack.ehr.dto;

import com.meditrack.ehr.model.PrescriptionStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PrescriptionDTO {
    private Long id;
    private String prescriptionNumber;
    private Long patientId;
    private Long doctorId;
    private Long medicalRecordId;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private PrescriptionStatus status;
    private String notes;
    private List<PrescriptionItemDTO> items;
    private LocalDateTime createdAt;
}
