package com.sejongmento.backend.domain.user.presentation.dto.response;

public record UserAuthDTO(long id, long tokenVersion, String stage) {}
