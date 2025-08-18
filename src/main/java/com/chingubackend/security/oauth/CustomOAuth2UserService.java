package com.chingubackend.security.oauth;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // google or kakao

        if ("google".equals(registrationId)) {
            return mapGoogleUser(oAuth2User);
        } else if ("kakao".equals(registrationId)) {
            return mapKakaoUser(oAuth2User);
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 Provider: " + registrationId);
        }
    }

    private CustomOAuth2User mapGoogleUser(OAuth2User oAuth2User) {
        Map<String, Object> attrs = oAuth2User.getAttributes();

        String provider = "GOOGLE";
        String providerId = (String) attrs.get("sub");
        String email = (String) attrs.get("email");
        String nickname = (String) attrs.get("name");
        String profileImage = (String) attrs.get("picture");

        return new CustomOAuth2User(
                provider,
                providerId,
                email,
                nickname,
                profileImage,
                attrs,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    private CustomOAuth2User mapKakaoUser(OAuth2User oAuth2User) {
        Map<String, Object> attrs = oAuth2User.getAttributes();

        String provider = "KAKAO";
        String providerId = String.valueOf(attrs.get("id"));

        Map<String, Object> account = (Map<String, Object>) attrs.get("kakao_account");
        String email = account != null ? (String) account.get("email") : null;

        Map<String, Object> profile = account != null ? (Map<String, Object>) account.get("profile") : null;
        String nickname = profile != null ? (String) profile.get("nickname") : null;
        String profileImage = profile != null ? (String) profile.get("profile_image_url") : null;

        return new CustomOAuth2User(
                provider,
                providerId,
                email,
                nickname,
                profileImage,
                attrs,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}