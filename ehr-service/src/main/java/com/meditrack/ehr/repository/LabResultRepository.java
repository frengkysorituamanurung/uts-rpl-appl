package com.meditrack.ehr.repository;

import com.meditrack.ehr.model.LabResult;
import com.meditrack.ehr.model.ResultStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabResultRepository extends JpaRepository<LabResult, Long> {
    Optional<LabResult> findByResultNumber(String resultNumber);
    List<LabResult> findByPatientId(Long patientId);
    List<LabResult> findByDoctorId(Long doctorId);
    List<LabResult> findByStatus(ResultStatus status);
    List<LabResult> findByPatientIdOrderByTestDateDesc(Long patientId);
}
