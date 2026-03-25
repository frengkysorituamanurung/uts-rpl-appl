package com.meditrack.appointment.repository;

import com.meditrack.appointment.model.Appointment;
import com.meditrack.appointment.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByDoctorIdAndAppointmentDateTimeBetween(
        Long doctorId, 
        LocalDateTime start, 
        LocalDateTime end
    );
    boolean existsByDoctorIdAndAppointmentDateTimeAndStatusNot(
        Long doctorId, 
        LocalDateTime appointmentDateTime, 
        AppointmentStatus status
    );
}
