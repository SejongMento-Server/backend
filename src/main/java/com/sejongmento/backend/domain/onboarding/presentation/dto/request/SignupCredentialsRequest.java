package com.sejongmento.backend.domain.onboarding.presentation.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupCredentialsRequest(@NotBlank String regToken,
                                       @NotBlank String email,
                                       @NotBlank @Size(min = 8, max = 72, message = "비밀번호는 8~72자여야 합니다.")
                                       //요구사항대로 리팩토링 필요
                                       @NotBlank String password,
                                       @NotBlank String confirmNewPassword) {
    @AssertTrue(message = "새 비밀번호와 확인 비밀번호가 일치하지 않습니다.")
    public boolean isPasswordConfirmed() {
        return password != null && password.equals(confirmNewPassword);
    }
}
