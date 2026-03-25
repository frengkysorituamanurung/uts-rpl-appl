package com.meditrack.ehr.service;

import com.meditrack.ehr.dto.CreateMedicalRecordRequest;
import com.meditrack.ehr.dto.MedicalRecordDTO;
import com.meditrack.ehr.model.MedicalRecord;
import com.meditrack.ehr.model.RecordStatus;
import com.meditrack.ehr.repository.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {
    
    private final MedicalRecordRepository medicalRecordRepository;

    @Transactional
    public MedicalRecordDTO createMedicalRecord(CreateMedicalRecordRequest request) {
        MedicalRecord record = new MedicalRecord();
        record.setPatientId(request.getPatientId());
        record.setDoctorId(request.getDoctorId());
        record.setAppointmentId(request.getAppointmentId());
        record.setVisitDate(request.getVisitDate() != null ? request.getVisitDate() : LocalDateTime.now());
        record.setChiefComplaint(request.getChiefComplaint());
        record.setDiagnosis(request.getDiagnosis());
        record.setTreatmentPlan(request.getTreatmentPlan());
        record.setNotes(request.getNotes());
        
        // Vital Signs
        record.setTemperature(request.getTemperature());
        record.setBloodPressureSystolic(request.getBloodPressureSystolic());
        record.setBloodPressureDiastolic(request.getBloodPressureDiastolic());
        record.setHeartRate(request.getHeartRate());
        record.setRespiratoryRate(request.getRespiratoryRate());
        record.setWeight(request.getWeight());
        record.setHeight(request.getHeight());
        record.setOxygenSaturation(request.getOxygenSaturation());
        
        record = medicalRecordRepository.save(record);
        
        System.out.println("✅ Medical record created: " + record.getId());
        
        return convertToDTO(record);
    }

    public MedicalRecordDTO getMedicalRecordById(Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical record not found"));
        return convertToDTO(record);
    }

    public List<MedicalRecordDTO> getMedicalRecordsByPatient(Long patientId) {
        return medicalRecordRepository.findByPatientIdOrderByVisitDateDesc(patientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<MedicalRecordDTO> getMedicalRecordsByDoctor(Long doctorId) {
        return medicalRecordRepository.findByDoctorId(doctorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MedicalRecordDTO updateMedicalRecord(Long id, CreateMedicalRecordRequest request) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical record not found"));

        if (record.getStatus() == RecordStatus.FINALIZED) {
            throw new RuntimeException("Cannot update finalized medical record");
        }

        record.setChiefComplaint(request.getChiefComplaint());
        record.setDiagnosis(request.getDiagnosis());
        record.setTreatmentPlan(request.getTreatmentPlan());
        record.setNotes(request.getNotes());
        record.setUpdatedAt(LocalDateTime.now());

        record = medicalRecordRepository.save(record);
        
        System.out.println("✅ Medical record updated: " + record.getId());
        
        return convertToDTO(record);
    }

    @Transactional
    public MedicalRecordDTO finalizeMedicalRecord(Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical record not found"));

        record.setStatus(RecordStatus.FINALIZED);
        record.setUpdatedAt(LocalDateTime.now());

        record = medicalRecordRepository.save(record);
        
        System.out.println("✅ Medical record finalized: " + record.getId());
        
        return convertToDTO(record);
    }

    private MedicalRecordDTO convertToDTO(MedicalRecord record) {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setId(record.getId());
        dto.setPatientId(record.getPatientId());
        dto.setDoctorId(record.getDoctorId());
        dto.setAppointmentId(record.getAppointmentId());
        dto.setVisitDate(record.getVisitDate());
        dto.setChiefComplaint(record.getChiefComplaint());
        dto.setDiagnosis(record.getDiagnosis());
        dto.setTreatmentPlan(record.getTreatmentPlan());
        dto.setNotes(record.getNotes());
        dto.setTemperature(record.getTemperature());
        dto.setBloodPressureSystolic(record.getBloodPressureSystolic());
        dto.setBloodPressureDiastolic(record.getBloodPressureDiastolic());
        dto.setHeartRate(record.getHeartRate());
        dto.setRespiratoryRate(record.getRespiratoryRate());
        dto.setWeight(record.getWeight());
        dto.setHeight(record.getHeight());
        dto.setOxygenSaturation(record.getOxygenSaturation());
        dto.setStatus(record.getStatus());
        dto.setCreatedAt(record.getCreatedAt());
        return dto;
    }
}
