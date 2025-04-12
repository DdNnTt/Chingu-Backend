package com.chingubackend.entity;

import com.chingubackend.model.RequestStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_request")
@Getter
@NoArgsConstructor
public class GroupInvite{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus requestStatus;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public GroupInvite(Group group, User sender, User receiver, RequestStatus requestStatus) {
        this.group = group;
        this.sender = sender;
        this.receiver = receiver;
        this.requestStatus = requestStatus;
        this.createdAt = LocalDateTime.now();
    }

    public void updateStatus(RequestStatus newStatus) {
        this.requestStatus = newStatus;
    }
}