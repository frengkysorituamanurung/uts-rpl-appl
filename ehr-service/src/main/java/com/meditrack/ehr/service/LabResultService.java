package com.meditrack.ehr.service;

import com.meditrack.ehr.dto.CreateLabResultRequest;
import com.meditrack.ehr.dto.LabResultDTO;
import com.meditrack.ehr.model.LabResult;
import com.meditrack.ehr.model.ResultStatus;
import com.meditrack.ehr.repository.LabResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabResultService {
    
    private final LabResultRepository labResultRepository;

    @Transactional
    public LabResultDTO createLabResult(CreateLabResultRequest request) {
        LabResult labResult = new LabResult();
        labResult.setResultNumber("LAB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        labResult.setPatientId(request.getPatientId());
        labResult.setDoctorId(request.getDoctorId());
        labResult.setMedicalRecordId(request.getMedicalRecordId());
        labResult.setTestName(request.getTestName());
        labResult.setTestType(request.getTestType());
        labResult.setTestDate(request.getTestDate() != null ? request.getTestDate() : LocalDate.now());
        labResult.setResultDate(request.getResultDate());
        labResult.setResult(request.getResult());
        labResult.setNormalRange(request.getNormalRange());
        labResult.setUnit(request.getUnit());
        labResult.setLabName(request.getLabName());
        labResult.setTechnician(request.getTechnician());
        labResult.setNotes(request.getNotes());

        labResult = labResultRepository.save(labResult);
        
        // TODO: Publish LabResultAvailable event
        System.out.println("✅ Lab result created: " + labResult.getResultNumber());
        
        return convertToDTO(labResult);
    }

    public LabResultDTO getLabResultById(Long id) {
        LabResult labResult = labResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lab result not found"));
        return convertToDTO(labResult);
    }

    public List<LabResultDTO> getLabResultsByPatient(Long patientId) {
        return labResultRepository.findByPatientIdOrderByTestDateDesc(patientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public LabResultDTO updateLabResultStatus(Long id, ResultStatus status) {
        LabResult labResult = labResultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lab result not found"));

        labResult.setStatus(status);
        labResult = labResultRepository.save(labResult);
        
        System.out.println("✅ Lab result status updated: " + labResult.getResultNumber() + " -> " + status);
        
        return convertToDTO(labResult);
    }

    private LabResultDTO convertToDTO(LabResult labResult) {
        LabResultDTO dto = new LabResultDTO();
        dto.setId(labResult.getId());
        dto.setResultNumber(labResult.getResultNumber());
        dto.setPatientId(labResult.getPatientId());
        dto.setDoctorId(labResult.getDoctorId());
        dto.setMedicalRecordId(labResult.getMedicalRecordId());
        dto.setTestName(labResult.getTestName());
        dto.setTestType(labResult.getTestType());
        dto.setTestDate(labResult.getTestDate());
        dto.setResultDate(labResult.getResultDate());
        dto.setResult(labResult.getResult());
        dto.setNormalRange(labResult.getNormalRange());
        dto.setUnit(labResult.getUnit());
        dto.setStatus(labResult.getStatus());
        dto.setLabName(labResult.getLabName());
        dto.setTechnician(labResult.getTechnician());
        dto.setNotes(labResult.getNotes());
        dto.setCreatedAt(labResult.getCreatedAt());
        return dto;
    }
}
