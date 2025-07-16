package com.chingubackend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "friendship_scores")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendshipScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long friendUserId;
    private int score;
    @Column(name = "last_updated")
    private Timestamp lastUpdated;

    public int getScore() {
        return score;
    }
}