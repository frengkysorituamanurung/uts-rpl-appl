package com.meditrack.ehr.dto;

import lombok.Data;

@Data
public class PrescriptionItemDTO {
    private Long id;
    private String medicineName;
    private String dosage;
    private String frequency;
    private Integer durationDays;
    private String instructions;
    private Integer quantity;
}
