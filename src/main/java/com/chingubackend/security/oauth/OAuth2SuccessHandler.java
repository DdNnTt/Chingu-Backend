package com.chingubackend.security.oauth;

import com.chingubackend.entity.User;
import com.chingubackend.jwt.JwtUtil;
import com.chingubackend.model.SocialType;
import com.chingubackend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.chingubackend.model.Role;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        System.out.println("✅ OAuth2SuccessHandler 진입, JWT 발급 중...");

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String provider = oAuth2User.getProvider();          // GOOGLE or KAKAO
        String providerId = oAuth2User.getProviderId();      // sub or kakao id
        String uniqueKey = provider + ":" + providerId;      // Unique Key
        String email = oAuth2User.getEmail();
        String nickname = oAuth2User.getNickname();
        String profileImage = oAuth2User.getProfileImage();

        // DB에서 회원 조회
        Optional<User> optionalUser = userRepository.findByUniqueKey(uniqueKey);

        User user;
        if (optionalUser.isPresent()) {
            // 기존 회원 로그인
            user = optionalUser.get();
            user.setLastLoginDate(LocalDateTime.now());
            userRepository.save(user);
        } else {
            // 신규 회원 가입
            user = User.builder()
                    .userId(provider.toLowerCase() + "_" + providerId) // 내부용
                    .name(nickname != null ? nickname : provider + "_user")
                    .nickname(generateUniqueNickname(nickname, providerId))
                    .email(email != null ? email : provider.toLowerCase() + "_" + providerId + "@placeholder.local")
                    .profilePictureUrl(profileImage)
                    .socialType(SocialType.valueOf(provider)) // GOOGLE, KAKAO
                    .uniqueKey(uniqueKey)
                    .password("{noop}")
                    .role(Role.ROLE_USER)
                    .build();

            user = userRepository.save(user);
        }

        // JWT 발급 (DB 기반 정보)
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getNickname());

        // 프론트 리다이렉트
        String redirectUrl = "https://chinguchingu.vercel.app/oauth2/success?token=" + token;
        response.sendRedirect(redirectUrl);
    }

    private String generateUniqueNickname(String base, String providerId) {
        String seed = (base != null && !base.isBlank()) ? base : "user";
        String candidate = seed;
        int suffix = 0;
        while (userRepository.existsByNickname(candidate)) {
            suffix++;
            candidate = seed + "_" + providerId.substring(Math.max(0, providerId.length() - 4)) + "_" + suffix;
        }
        return candidate;
    }
}