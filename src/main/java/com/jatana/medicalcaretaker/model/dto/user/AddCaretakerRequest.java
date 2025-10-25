package com.jatana.medicalcaretaker.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AddCaretakerRequest(
        @NotBlank @Email String caretakerEmail
) {}


