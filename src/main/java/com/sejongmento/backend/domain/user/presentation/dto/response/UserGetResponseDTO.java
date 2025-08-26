package com.sejongmento.backend.domain.user.presentation.dto.response;

import com.sejongmento.backend.domain.profile.domain.entity.Profile;
import com.sejongmento.backend.domain.user.domain.entity.User;
import lombok.Builder;

@Builder
public record UserGetResponseDTO(
        long id,
        String userId,
        String stage,
        String sejongStudentId,
        ProfileDTO profile
) {
    @Builder
    public record ProfileDTO(
            Long id,
            String nickname,
            String major,
            String interest,
            String desiredJob,
            String bio,
            String imageUrl
    ) {}

    public static UserGetResponseDTO of(User user) {
        Profile p = user.getProfile();
        ProfileDTO profileDTO = (p == null) ? null :
                ProfileDTO.builder()
                        .id(p.getId())
                        .nickname(p.getNickname())
                        .major(p.getMajor())
                        .interest(p.getInterest())
                        .desiredJob(p.getDesiredJob())
                        .bio(p.getBio())
                        .imageUrl(p.getImageUrl())
                        .build();

        return UserGetResponseDTO.builder()
                .id(user.getId())
                .userId(user.getLoginInfo() != null ? user.getLoginInfo().getUserId() : null)
                .stage(user.getStage().name())
                .sejongStudentId(user.getSejongStudentId())
                .profile(profileDTO)
                .build();
    }
}