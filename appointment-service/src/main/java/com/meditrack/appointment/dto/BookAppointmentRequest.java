package com.meditrack.appointment.dto;

import com.meditrack.appointment.model.AppointmentType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookAppointmentRequest {
    private Long patientId;
    private Long doctorId;
    private LocalDateTime appointmentDateTime;
    private Integer durationMinutes = 30;
    private AppointmentType type = AppointmentType.CONSULTATION;
    private String reasonForVisit;
    private String symptoms;
}
