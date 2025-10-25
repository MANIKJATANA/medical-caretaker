package com.jatana.medicalcaretaker.model.dto.user;

import com.jatana.medicalcaretaker.model.userEnums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateUserRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String password,
        @NotNull Role role,
        @NotBlank String mobileNumber
) {}


