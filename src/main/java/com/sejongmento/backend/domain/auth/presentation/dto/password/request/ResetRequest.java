package com.sejongmento.backend.domain.auth.presentation.dto.password.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetRequest(
        @Email @NotBlank String email
) {}
