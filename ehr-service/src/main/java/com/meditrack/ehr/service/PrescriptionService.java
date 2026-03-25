package com.meditrack.ehr.service;

import com.meditrack.ehr.dto.CreatePrescriptionRequest;
import com.meditrack.ehr.dto.PrescriptionDTO;
import com.meditrack.ehr.dto.PrescriptionItemDTO;
import com.meditrack.ehr.model.Prescription;
import com.meditrack.ehr.model.PrescriptionItem;
import com.meditrack.ehr.model.PrescriptionStatus;
import com.meditrack.ehr.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrescriptionService {
    
    private final PrescriptionRepository prescriptionRepository;

    @Transactional
    public PrescriptionDTO createPrescription(CreatePrescriptionRequest request) {
        Prescription prescription = new Prescription();
        prescription.setPrescriptionNumber("RX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        prescription.setPatientId(request.getPatientId());
        prescription.setDoctorId(request.getDoctorId());
        prescription.setMedicalRecordId(request.getMedicalRecordId());
        prescription.setIssueDate(request.getIssueDate() != null ? request.getIssueDate() : LocalDate.now());
        prescription.setExpiryDate(prescription.getIssueDate().plusDays(request.getValidityDays()));
        prescription.setNotes(request.getNotes());

        // Add prescription items
        if (request.getItems() != null) {
            for (PrescriptionItemDTO itemDTO : request.getItems()) {
                PrescriptionItem item = new PrescriptionItem();
                item.setPrescription(prescription);
                item.setMedicineName(itemDTO.getMedicineName());
                item.setDosage(itemDTO.getDosage());
                item.setFrequency(itemDTO.getFrequency());
                item.setDurationDays(itemDTO.getDurationDays());
                item.setInstructions(itemDTO.getInstructions());
                item.setQuantity(itemDTO.getQuantity());
                prescription.getItems().add(item);
            }
        }

        prescription = prescriptionRepository.save(prescription);
        
        // TODO: Publish PrescriptionIssued event
        System.out.println("✅ Prescription created: " + prescription.getPrescriptionNumber());
        
        return convertToDTO(prescription);
    }

    public PrescriptionDTO getPrescriptionById(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));
        return convertToDTO(prescription);
    }

    public PrescriptionDTO getPrescriptionByNumber(String prescriptionNumber) {
        Prescription prescription = prescriptionRepository.findByPrescriptionNumber(prescriptionNumber)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));
        return convertToDTO(prescription);
    }

    public List<PrescriptionDTO> getPrescriptionsByPatient(Long patientId) {
        return prescriptionRepository.findByPatientIdOrderByIssueDateDesc(patientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PrescriptionDTO updatePrescriptionStatus(Long id, PrescriptionStatus status) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        prescription.setStatus(status);
        prescription = prescriptionRepository.save(prescription);
        
        System.out.println("✅ Prescription status updated: " + prescription.getPrescriptionNumber() + " -> " + status);
        
        return convertToDTO(prescription);
    }

    private PrescriptionDTO convertToDTO(Prescription prescription) {
        PrescriptionDTO dto = new PrescriptionDTO();
        dto.setId(prescription.getId());
        dto.setPrescriptionNumber(prescription.getPrescriptionNumber());
        dto.setPatientId(prescription.getPatientId());
        dto.setDoctorId(prescription.getDoctorId());
        dto.setMedicalRecordId(prescription.getMedicalRecordId());
        dto.setIssueDate(prescription.getIssueDate());
        dto.setExpiryDate(prescription.getExpiryDate());
        dto.setStatus(prescription.getStatus());
        dto.setNotes(prescription.getNotes());
        dto.setCreatedAt(prescription.getCreatedAt());

        List<PrescriptionItemDTO> itemDTOs = prescription.getItems().stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);

        return dto;
    }

    private PrescriptionItemDTO convertItemToDTO(PrescriptionItem item) {
        PrescriptionItemDTO dto = new PrescriptionItemDTO();
        dto.setId(item.getId());
        dto.setMedicineName(item.getMedicineName());
        dto.setDosage(item.getDosage());
        dto.setFrequency(item.getFrequency());
        dto.setDurationDays(item.getDurationDays());
        dto.setInstructions(item.getInstructions());
        dto.setQuantity(item.getQuantity());
        return dto;
    }
}
