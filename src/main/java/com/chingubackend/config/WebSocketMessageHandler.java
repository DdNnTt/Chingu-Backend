package com.chingubackend.config;

import com.chingubackend.entity.User;
import com.chingubackend.jwt.JwtUtil;
import com.chingubackend.repository.MessageRepository;
import com.chingubackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class WebSocketMessageHandler implements WebSocketHandler {

    // 사용자 userId와 WebSocketSession을 매핑
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private final JwtUtil jwtUtil;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    // WebSocket 연결 성공 시 실행
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = extractToken(session);

        if (token == null || token.isBlank()) {
            session.sendMessage(new TextMessage("token 파라미터가 필요합니다."));
            session.close();
            return;
        }

        // JWT 유효성 검사
        if (!jwtUtil.validateToken(token)) {
            session.sendMessage(new TextMessage("유효하지 않은 토큰입니다."));
            session.close();
            return;
        }

        // JWT에서 userId 추출 (sub 필드)
        String userId = jwtUtil.extractUsername(token);
        if (userId == null) {
            session.sendMessage(new TextMessage("토큰에서 사용자 정보를 추출할 수 없습니다."));
            session.close();
            return;
        }

        // DB에서 사용자 검증
        Optional<User> user = userRepository.findByUserId(userId);
        if (user.isEmpty()) {
            session.sendMessage(new TextMessage("존재하지 않는 사용자입니다."));
            session.close();
            return;
        }

        // 연결 유지 + 세션 저장
        sessionMap.put(userId, session);

        // 읽지 않은 쪽지 개수 전송
        Long unreadCount = messageRepository.countByReceiverIdAndReadStatus(user.get().getId(), false);
        session.sendMessage(new TextMessage("읽지 않은 쪽지의 개수: " + unreadCount));
    }

    // 클라이언트로부터 메시지를 받을 때
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {
        // 현재 쪽지 기능에서는 메시지 수신 처리 필요 없음
    }

    // 오류 발생 시
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        exception.printStackTrace(); // 또는 로깅
    }

    // 연결 종료 시
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String token = extractToken(session);
        if (token != null && jwtUtil.validateToken(token)) {
            String userId = jwtUtil.extractUsername(token);
            if (userId != null) {
                sessionMap.remove(userId);
            }
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    // WebSocket URL에서 token 파라미터 추출
    private String extractToken(WebSocketSession session) {
        return UriComponentsBuilder.fromUri(session.getUri()).build()
                .getQueryParams().getFirst("token");
    }

    // 특정 사용자에게 메시지 전송
    public void sendNotification(String userId, String message) throws Exception {
        WebSocketSession session = sessionMap.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }
}
