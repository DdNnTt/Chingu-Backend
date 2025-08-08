package com.chingubackend.security.oauth;

import com.chingubackend.entity.User;
import com.chingubackend.model.Role;
import com.chingubackend.model.SocialType;
import com.chingubackend.repository.UserRepository;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
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

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("✅ CustomOAuth2UserService 호출됨");

        try {
            OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);
            Map<String, Object> attributes = oAuth2User.getAttributes();

            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");
            String picture = (String) attributes.get("picture");

            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        User newUser = User.builder()
                                .userId(UUID.randomUUID().toString())
                                .name(name)
                                .nickname("user_" + UUID.randomUUID().toString().substring(0, 8))
                                .email(email)
                                .password("NO_PASSWORD")
                                .profilePictureUrl(picture)
                                .socialType(SocialType.GOOGLE)
                                .role(Role.ROLE_USER)
                                .build();
                        return userRepository.save(newUser);
                    });

            return new CustomOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                    attributes,
                    "email",
                    user.getId(),
                    user.getEmail()
            );

        } catch (Exception e) {
            System.out.println("❌ CustomOAuth2UserService 예외 발생: " + e.getMessage());
            e.printStackTrace();  // 콘솔에 전체 스택 출력
            throw new OAuth2AuthenticationException(e.getMessage());
        }
    }
}