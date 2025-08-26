package com.sejongmento.backend.domain.profile.application;

import com.sejongmento.backend.domain.profile.infra.jpa.ProfileRepository;
import com.sejongmento.backend.domain.profile.domain.entity.Profile;
import com.sejongmento.backend.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    @Transactional
    public Long createBasicFor(User user, String nickname, String major, String desiredJob) {
        Profile p = Profile.builder()
                .nickname(nickname)
                .major(major)
                .desiredJob(desiredJob)
                .build();
        profileRepository.save(p);
        user.attachProfile(p); // 양방향 동기화 (owner = User, FK=users.profile_id)

        return p.getId();
    }

    @Transactional
    public Profile updateInitial(Profile profile, String nickname, String major, String desiredJob) {
        profile.updateInitial(nickname, major, /* interest */ null, desiredJob);
        return profile;
    }
}