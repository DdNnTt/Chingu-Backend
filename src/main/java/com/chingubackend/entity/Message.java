package com.chingubackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity(name = "message")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId; // 쪽지 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender; // 쪽지 송신자 회원 번호
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="receiver_id")
    private User receiver; // 쪽지 수신자 회원 번호
    private String content; // 내용

    @CreationTimestamp // 현재 시간 자동 삽입
    private LocalDateTime sendTime; // 송신 시간
    @ColumnDefault("FALSE")
    private boolean readStatus; // 읽음 여부
}
