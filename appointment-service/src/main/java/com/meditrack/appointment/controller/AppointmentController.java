package com.meditrack.appointment.controller;

import com.meditrack.appointment.dto.AppointmentDTO;
import com.meditrack.appointment.dto.BookAppointmentRequest;
import com.meditrack.appointment.dto.RescheduleRequest;
import com.meditrack.appointment.model.AppointmentStatus;
import com.meditrack.appointment.service.AppointmentService;
import com.meditrack.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    
    private final AppointmentService appointmentService;

    @PostMapping
    public ApiResponse<AppointmentDTO> bookAppointment(@RequestBody BookAppointmentRequest request) {
        try {
            AppointmentDTO appointment = appointmentService.bookAppointment(request);
            return ApiResponse.success("Appointment booked successfully", appointment);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/reschedule")
    public ApiResponse<AppointmentDTO> rescheduleAppointment(
            @PathVariable Long id,
            @RequestBody RescheduleRequest request) {
        try {
            AppointmentDTO appointment = appointmentService.rescheduleAppointment(id, request);
            return ApiResponse.success("Appointment rescheduled successfully", appointment);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> cancelAppointment(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        try {
            String reason = body != null ? body.get("reason") : "No reason provided";
            appointmentService.cancelAppointment(id, reason);
            return ApiResponse.success("Appointment cancelled successfully", null);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<AppointmentDTO> getAppointmentById(@PathVariable Long id) {
        try {
            AppointmentDTO appointment = appointmentService.getAppointmentById(id);
            return ApiResponse.success(appointment);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/patient/{patientId}")
    public ApiResponse<List<AppointmentDTO>> getAppointmentsByPatient(@PathVariable Long patientId) {
        try {
            List<AppointmentDTO> appointments = appointmentService.getAppointmentsByPatient(patientId);
            return ApiResponse.success(appointments);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/doctor/{doctorId}")
    public ApiResponse<List<AppointmentDTO>> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        try {
            List<AppointmentDTO> appointments = appointmentService.getAppointmentsByDoctor(doctorId);
            return ApiResponse.success(appointments);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/status/{status}")
    public ApiResponse<List<AppointmentDTO>> getAppointmentsByStatus(@PathVariable AppointmentStatus status) {
        try {
            List<AppointmentDTO> appointments = appointmentService.getAppointmentsByStatus(status);
            return ApiResponse.success(appointments);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ApiResponse<AppointmentDTO> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            AppointmentStatus status = AppointmentStatus.valueOf(body.get("status"));
            AppointmentDTO appointment = appointmentService.updateAppointmentStatus(id, status);
            return ApiResponse.success("Appointment status updated successfully", appointment);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
