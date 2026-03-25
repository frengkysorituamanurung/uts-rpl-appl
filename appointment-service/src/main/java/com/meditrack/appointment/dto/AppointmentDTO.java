package com.meditrack.appointment.dto;

import com.meditrack.appointment.model.AppointmentStatus;
import com.meditrack.appointment.model.AppointmentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentDTO {
    private Long id;
    private Long patientId;
    private Long doctorId;
    private LocalDateTime appointmentDateTime;
    private Integer durationMinutes;
    private AppointmentType type;
    private AppointmentStatus status;
    private String reasonForVisit;
    private String symptoms;
    private String notes;
    private LocalDateTime createdAt;
}
