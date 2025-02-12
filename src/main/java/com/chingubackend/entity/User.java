package com.chingubackend.entity;

import com.chingubackend.model.SocialType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String profilePictureUrl;

    private String bio;

    private LocalDateTime joinDate;

    private LocalDateTime lastLoginDate;

    private String uniqueKey;

    @Enumerated(EnumType.STRING)
    private SocialType socialType = SocialType.NONE;

    @PrePersist
    public void prePersist() {
        if (joinDate == null) {
            joinDate = LocalDateTime.now();  // 현재 시간으로 joinDate 설정
        }
    }

    @PreUpdate
    public void preUpdate() {
        lastLoginDate = LocalDateTime.now();
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    @Builder
    public User(String userId, String name, String nickname, String email, String password, String profilePictureUrl, String bio, SocialType socialType) {
        this.userId = userId;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profilePictureUrl = profilePictureUrl;
        this.bio = bio;
        this.socialType = socialType;
    }
}
