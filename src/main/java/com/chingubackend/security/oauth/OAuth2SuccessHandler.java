package com.chingubackend.security.oauth;

import com.chingubackend.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil; // ✅ JwtUtil 주입

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        System.out.println("✅ OAuth2SuccessHandler 진입, JWT 발급 중...");

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        Long userId = oAuth2User.getUserId();
        String email = oAuth2User.getEmail();
        String nickname = oAuth2User.getAttributes().get("name").toString(); // 또는 DB에서 가져온 nickname

        String token = jwtUtil.generateToken(userId, email, nickname); // ✅ JWT 발급

        // 프론트로 리디렉션 + JWT 전달
        String redirectUrl = "https://chinguchingu.vercel.app/oauth2/success?token=" + token;
        response.sendRedirect(redirectUrl);
    }
}