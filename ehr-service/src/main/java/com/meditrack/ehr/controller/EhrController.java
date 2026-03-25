package com.meditrack.ehr.controller;

import com.meditrack.common.dto.ApiResponse;
import com.meditrack.ehr.dto.*;
import com.meditrack.ehr.model.PrescriptionStatus;
import com.meditrack.ehr.model.ResultStatus;
import com.meditrack.ehr.service.LabResultService;
import com.meditrack.ehr.service.MedicalRecordService;
import com.meditrack.ehr.service.PrescriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ehr")
@RequiredArgsConstructor
public class EhrController {
    
    private final MedicalRecordService medicalRecordService;
    private final PrescriptionService prescriptionService;
    private final LabResultService labResultService;

    // ==================== Medical Records ====================
    
    @PostMapping("/records")
    public ApiResponse<MedicalRecordDTO> createMedicalRecord(@RequestBody CreateMedicalRecordRequest request) {
        try {
            MedicalRecordDTO record = medicalRecordService.createMedicalRecord(request);
            return ApiResponse.success("Medical record created successfully", record);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/records/{id}")
    public ApiResponse<MedicalRecordDTO> getMedicalRecordById(@PathVariable Long id) {
        try {
            MedicalRecordDTO record = medicalRecordService.getMedicalRecordById(id);
            return ApiResponse.success(record);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/records/patient/{patientId}")
    public ApiResponse<List<MedicalRecordDTO>> getMedicalRecordsByPatient(@PathVariable Long patientId) {
        try {
            List<MedicalRecordDTO> records = medicalRecordService.getMedicalRecordsByPatient(patientId);
            return ApiResponse.success(records);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/records/doctor/{doctorId}")
    public ApiResponse<List<MedicalRecordDTO>> getMedicalRecordsByDoctor(@PathVariable Long doctorId) {
        try {
            List<MedicalRecordDTO> records = medicalRecordService.getMedicalRecordsByDoctor(doctorId);
            return ApiResponse.success(records);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/records/{id}")
    public ApiResponse<MedicalRecordDTO> updateMedicalRecord(
            @PathVariable Long id,
            @RequestBody CreateMedicalRecordRequest request) {
        try {
            MedicalRecordDTO record = medicalRecordService.updateMedicalRecord(id, request);
            return ApiResponse.success("Medical record updated successfully", record);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/records/{id}/finalize")
    public ApiResponse<MedicalRecordDTO> finalizeMedicalRecord(@PathVariable Long id) {
        try {
            MedicalRecordDTO record = medicalRecordService.finalizeMedicalRecord(id);
            return ApiResponse.success("Medical record finalized successfully", record);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // ==================== Prescriptions ====================
    
    @PostMapping("/prescriptions")
    public ApiResponse<PrescriptionDTO> createPrescription(@RequestBody CreatePrescriptionRequest request) {
        try {
            PrescriptionDTO prescription = prescriptionService.createPrescription(request);
            return ApiResponse.success("Prescription created successfully", prescription);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/prescriptions/{id}")
    public ApiResponse<PrescriptionDTO> getPrescriptionById(@PathVariable Long id) {
        try {
            PrescriptionDTO prescription = prescriptionService.getPrescriptionById(id);
            return ApiResponse.success(prescription);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/prescriptions/number/{prescriptionNumber}")
    public ApiResponse<PrescriptionDTO> getPrescriptionByNumber(@PathVariable String prescriptionNumber) {
        try {
            PrescriptionDTO prescription = prescriptionService.getPrescriptionByNumber(prescriptionNumber);
            return ApiResponse.success(prescription);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/prescriptions/patient/{patientId}")
    public ApiResponse<List<PrescriptionDTO>> getPrescriptionsByPatient(@PathVariable Long patientId) {
        try {
            List<PrescriptionDTO> prescriptions = prescriptionService.getPrescriptionsByPatient(patientId);
            return ApiResponse.success(prescriptions);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/prescriptions/{id}/status")
    public ApiResponse<PrescriptionDTO> updatePrescriptionStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            PrescriptionStatus status = PrescriptionStatus.valueOf(body.get("status"));
            PrescriptionDTO prescription = prescriptionService.updatePrescriptionStatus(id, status);
            return ApiResponse.success("Prescription status updated successfully", prescription);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // ==================== Lab Results ====================
    
    @PostMapping("/lab-results")
    public ApiResponse<LabResultDTO> createLabResult(@RequestBody CreateLabResultRequest request) {
        try {
            LabResultDTO labResult = labResultService.createLabResult(request);
            return ApiResponse.success("Lab result created successfully", labResult);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/lab-results/{id}")
    public ApiResponse<LabResultDTO> getLabResultById(@PathVariable Long id) {
        try {
            LabResultDTO labResult = labResultService.getLabResultById(id);
            return ApiResponse.success(labResult);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/lab-results/patient/{patientId}")
    public ApiResponse<List<LabResultDTO>> getLabResultsByPatient(@PathVariable Long patientId) {
        try {
            List<LabResultDTO> labResults = labResultService.getLabResultsByPatient(patientId);
            return ApiResponse.success(labResults);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/lab-results/{id}/status")
    public ApiResponse<LabResultDTO> updateLabResultStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            ResultStatus status = ResultStatus.valueOf(body.get("status"));
            LabResultDTO labResult = labResultService.updateLabResultStatus(id, status);
            return ApiResponse.success("Lab result status updated successfully", labResult);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
