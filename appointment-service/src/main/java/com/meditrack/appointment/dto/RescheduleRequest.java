package com.meditrack.appointment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RescheduleRequest {
    private LocalDateTime newAppointmentDateTime;
    private String reason;
}
