package com.meditrack.ehr.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "prescription_items")
public class PrescriptionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @Column(nullable = false)
    private String medicineName;

    private String dosage;
    private String frequency;
    private Integer durationDays;
    
    @Column(length = 500)
    private String instructions;
    
    private Integer quantity;
}
