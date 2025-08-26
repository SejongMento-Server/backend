package com.sejongmento.backend.domain.user.application;

import com.sejongmento.backend.domain.user.presentation.dto.response.UserAuthDTO;
import com.sejongmento.backend.domain.user.presentation.dto.response.UserGetResponseDTO;

public interface UserGetService {
    UserGetResponseDTO getUserById(final long id);
    UserGetResponseDTO getUserByUserId(final String userId);
    UserAuthDTO getAuthById(long id);
}