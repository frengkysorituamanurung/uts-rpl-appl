package com.meditrack.user.dto;

import com.meditrack.user.model.UserRole;
import com.meditrack.user.model.UserStatus;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private UserRole role;
    private UserStatus status;
}
