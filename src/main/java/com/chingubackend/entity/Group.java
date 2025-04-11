package com.chingubackend.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_groups") // 예약어 회피
@NoArgsConstructor
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name", length = 100, nullable = false)
    private String groupName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Group(String groupName, String description) {
        this.groupName = groupName;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}