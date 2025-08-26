package com.sejongmento.backend.domain.user.domain.entity;

import com.sejongmento.backend.domain.profile.domain.entity.Profile;
import com.sejongmento.backend.domain.user.domain.entity.value.LoginInfo;
import com.sejongmento.backend.domain.user.domain.enums.MemberStage;
import com.sejongmento.backend.global.common.BaseEntity;
import com.sejongmento.backend.global.exception.common.ApplicationException;
import com.sejongmento.backend.global.exception.user.UserErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Embedded
    private LoginInfo loginInfo;

    @OneToOne(cascade = {CascadeType.REMOVE})
    @JoinColumn(name = "profile_id", unique = true)
    private Profile profile;

    @Column(unique = true)
    private String sejongStudentId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false)
    private MemberStage stage = MemberStage.UNVERIFIED;


    @Builder.Default
    @Column(nullable = false)
    private long tokenVersion = 1L;
    public void increaseTokenVersion() { this.tokenVersion++; }


    public void activate() { this.stage = MemberStage.ACTIVE; }

    /** 프로필 연결: 이미 다른 프로필이 붙어있으면 차단 */
    public void attachProfile(Profile profile) {
        if (this.profile != null && !Objects.equals(this.profile.getId(), profile != null ? profile.getId() : null)) {
            throw new ApplicationException(UserErrorCode.PROFILE_ALREADY_ATTACHED);
        }
        this.profile = profile;
        if (profile != null) profile.addUser(this);
    }

    /** 이메일/비밀번호 저장: 이메일 변경 시도 방지 */
    public void saveCredentials(String emailLower, String rawPassword, PasswordEncoder encoder) {
        if (this.loginInfo == null || this.loginInfo.getUserId() == null) {
            this.loginInfo = LoginInfo.builder()
                    .userId(emailLower)
                    .password(rawPassword)
                    .build();
        } else if (!this.loginInfo.getUserId().equalsIgnoreCase(emailLower)) {

            throw new ApplicationException(UserErrorCode.EMAIL_IMMUTABLE);
        } else {
            this.loginInfo = LoginInfo.builder()
                    .userId(this.loginInfo.getUserId())
                    .password(rawPassword)
                    .build();
        }
        this.loginInfo.encryptPassword(encoder);
    }
    public void changePassword(String rawPassword, PasswordEncoder encoder) {
        if (this.loginInfo == null || this.loginInfo.getUserId() == null) {
            throw new ApplicationException(UserErrorCode.CREDENTIALS_NOT_SET);
        }
        this.loginInfo = LoginInfo.builder()
                .userId(this.loginInfo.getUserId())
                .password(rawPassword)
                .build();
        this.loginInfo.encryptPassword(encoder);
    }

}
