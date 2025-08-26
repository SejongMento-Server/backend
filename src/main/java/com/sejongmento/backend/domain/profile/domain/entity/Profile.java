package com.sejongmento.backend.domain.profile.domain.entity;

import com.sejongmento.backend.domain.user.domain.entity.User;
import com.sejongmento.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "profile")
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile extends BaseEntity {
    @Id
    @Column(name = "profile_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "profile")
    private User user;

    @Column(nullable = true)
    private String imageUrl;//사진 필드

    @Column(nullable = false, length = 30)//임시
    private String nickname;

    @Column(name = "interest", length = 50)//복수로 바뀌게 되면 중간 테이블 둬야 할 듯
    private String interest;

    @Column(length = 50)//임시
    private String major;

    @Column(length = 50)
    private String desiredJob;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String bio;



    public void addUser(User user) { this.user = user; }

    public void updateInitial(String nickname, String major, String interest, String desiredJob) {
        if (nickname != null && !nickname.isBlank()) this.nickname = nickname.trim();
        this.major = major;
        this.desiredJob = desiredJob;
        this.interest = interest;
    }

    public void updateAll(String imageUrl, String nickname, String major,
                          String interest, String desiredJob, String bio) {
        this.imageUrl = imageUrl;
        if (nickname != null && !nickname.isBlank()) this.nickname = nickname.trim();
        this.major = major;
        this.desiredJob = desiredJob;
        this.interest = interest;
        this.bio = bio;
    }


}

