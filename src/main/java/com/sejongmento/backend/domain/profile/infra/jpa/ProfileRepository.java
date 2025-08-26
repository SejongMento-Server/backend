package com.sejongmento.backend.domain.profile.infra.jpa;

import com.sejongmento.backend.domain.profile.domain.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
