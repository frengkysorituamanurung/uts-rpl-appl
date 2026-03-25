package com.meditrack.appointment.service;

import com.meditrack.appointment.dto.AppointmentDTO;
import com.meditrack.appointment.dto.BookAppointmentRequest;
import com.meditrack.appointment.dto.RescheduleRequest;
import com.meditrack.appointment.model.Appointment;
import com.meditrack.appointment.model.AppointmentStatus;
import com.meditrack.appointment.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public AppointmentDTO bookAppointment(BookAppointmentRequest request) {
        // Validate appointment time is in the future
        if (request.getAppointmentDateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Appointment time must be in the future");
        }

        // Check if doctor is available at the requested time
        boolean doctorBusy = appointmentRepository.existsByDoctorIdAndAppointmentDateTimeAndStatusNot(
            request.getDoctorId(),
            request.getAppointmentDateTime(),
            AppointmentStatus.CANCELLED
        );

        if (doctorBusy) {
            throw new RuntimeException("Doctor is not available at the requested time");
        }

        Appointment appointment = new Appointment();
        appointment.setPatientId(request.getPatientId());
        appointment.setDoctorId(request.getDoctorId());
        appointment.setAppointmentDateTime(request.getAppointmentDateTime());
        appointment.setDurationMinutes(request.getDurationMinutes());
        appointment.setType(request.getType());
        appointment.setReasonForVisit(request.getReasonForVisit());
        appointment.setSymptoms(request.getSymptoms());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        appointment = appointmentRepository.save(appointment);
        
        // TODO: Publish AppointmentBooked event to message queue
        System.out.println("✅ Appointment booked: " + appointment.getId());
        
        return convertToDTO(appointment);
    }

    @Transactional
    public AppointmentDTO rescheduleAppointment(Long id, RescheduleRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Cannot reschedule a cancelled appointment");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Cannot reschedule a completed appointment");
        }

        // Validate new time is in the future
        if (request.getNewAppointmentDateTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("New appointment time must be in the future");
        }

        // Check if doctor is available at the new time
        boolean doctorBusy = appointmentRepository.existsByDoctorIdAndAppointmentDateTimeAndStatusNot(
            appointment.getDoctorId(),
            request.getNewAppointmentDateTime(),
            AppointmentStatus.CANCELLED
        );

        if (doctorBusy) {
            throw new RuntimeException("Doctor is not available at the requested time");
        }

        appointment.setAppointmentDateTime(request.getNewAppointmentDateTime());
        appointment.setStatus(AppointmentStatus.RESCHEDULED);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment.setNotes(appointment.getNotes() + "\nRescheduled: " + request.getReason());

        appointment = appointmentRepository.save(appointment);
        
        // TODO: Publish AppointmentRescheduled event
        System.out.println("✅ Appointment rescheduled: " + appointment.getId());
        
        return convertToDTO(appointment);
    }

    @Transactional
    public void cancelAppointment(Long id, String reason) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Appointment is already cancelled");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel a completed appointment");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelledAt(LocalDateTime.now());
        appointment.setCancellationReason(reason);
        appointment.setUpdatedAt(LocalDateTime.now());

        appointmentRepository.save(appointment);
        
        // TODO: Publish AppointmentCancelled event
        System.out.println("✅ Appointment cancelled: " + appointment.getId());
    }

    public AppointmentDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        return convertToDTO(appointment);
    }

    public List<AppointmentDTO> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentDTO updateAppointmentStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointment.setStatus(status);
        appointment.setUpdatedAt(LocalDateTime.now());

        appointment = appointmentRepository.save(appointment);
        
        System.out.println("✅ Appointment status updated: " + appointment.getId() + " -> " + status);
        
        return convertToDTO(appointment);
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setPatientId(appointment.getPatientId());
        dto.setDoctorId(appointment.getDoctorId());
        dto.setAppointmentDateTime(appointment.getAppointmentDateTime());
        dto.setDurationMinutes(appointment.getDurationMinutes());
        dto.setType(appointment.getType());
        dto.setStatus(appointment.getStatus());
        dto.setReasonForVisit(appointment.getReasonForVisit());
        dto.setSymptoms(appointment.getSymptoms());
        dto.setNotes(appointment.getNotes());
        dto.setCreatedAt(appointment.getCreatedAt());
        return dto;
    }
}
