package com.sejongmento.backend.domain.user.application.impl;

import com.sejongmento.backend.domain.user.application.UserGetService;
import com.sejongmento.backend.domain.user.infra.jpa.UserRepository;
import com.sejongmento.backend.domain.user.presentation.dto.response.UserAuthDTO;
import com.sejongmento.backend.domain.user.presentation.dto.response.UserGetResponseDTO;
import com.sejongmento.backend.global.exception.common.ApplicationException;
import com.sejongmento.backend.global.exception.user.UserErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserGetServiceImpl implements UserGetService {

    private final UserRepository userRepository;

    @Override
    public UserGetResponseDTO getUserById(final long id) {
        return userRepository.findById(id).map(UserGetResponseDTO::of)
                .orElseThrow(() -> new ApplicationException(UserErrorCode.NOT_EXISTS));
    }

    @Override
    public UserGetResponseDTO getUserByUserId(final String userId) {
        return userRepository.findByLoginInfoUserId(userId)
                .map(UserGetResponseDTO::of)
                .orElseThrow(() -> new ApplicationException(UserErrorCode.NOT_EXISTS));
    }
    @Override
    public UserAuthDTO getAuthById(final long id) {
        var u = userRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(UserErrorCode.NOT_EXISTS));

        return new UserAuthDTO(u.getId(), u.getTokenVersion(), u.getStage().name());
    }
}
