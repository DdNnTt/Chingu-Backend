package com.chingubackend.security.oauth;

import java.util.Collection;
import java.util.Map;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Getter
public class CustomOAuth2User implements OAuth2User {

    private final String provider;     // GOOGLE, KAKAO
    private final String providerId;   // sub (구글) / id (카카오)
    private final String email;
    private final String nickname;
    private final String profileImage;
    private final Map<String, Object> attributes;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomOAuth2User(String provider,
                            String providerId,
                            String email,
                            String nickname,
                            String profileImage,
                            Map<String, Object> attributes,
                            Collection<? extends GrantedAuthority> authorities) {
        this.provider = provider;
        this.providerId = providerId;
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.attributes = attributes;
        this.authorities = authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return provider + ":" + providerId; // unique key 역할
    }
}