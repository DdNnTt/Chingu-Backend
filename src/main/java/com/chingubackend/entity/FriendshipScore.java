package com.chingubackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.security.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "friendship_scores")
public class FriendshipScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long friendUserId;
    private int score;
    private Timestamp lastUpdated;

    public int getScore() {
        return score;
    }
}