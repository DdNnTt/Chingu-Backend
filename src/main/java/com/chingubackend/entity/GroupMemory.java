package com.chingubackend.entity;

import com.chingubackend.dto.request.GroupMemoryUpdateRequest;
import com.chingubackend.entity.Group;
import com.chingubackend.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "group_memories")
public class GroupMemory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url_1")
    private String imageUrl1;

    @Column(name = "image_url_2")
    private String imageUrl2;

    @Column(name = "image_url_3")
    private String imageUrl3;

    private String location;

    @Column(name = "memory_date")
    private LocalDate memoryDate;

    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "modify_date")
    private LocalDateTime modifyDate = LocalDateTime.now();

    @Builder
    public GroupMemory(Group group, User user, String title, String content,
                       String imageUrl1, String imageUrl2, String imageUrl3,
                       String location, LocalDate memoryDate) {
        this.group = group;
        this.user = user;
        this.title = title;
        this.content = content;
        this.imageUrl1 = imageUrl1;
        this.imageUrl2 = imageUrl2;
        this.imageUrl3 = imageUrl3;
        this.location = location;
        this.memoryDate = memoryDate;
        this.createdDate = LocalDateTime.now();
        this.modifyDate = LocalDateTime.now();
    }

    public void update(GroupMemoryUpdateRequest dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.imageUrl1 = dto.getImageUrl1();
        this.imageUrl2 = dto.getImageUrl2();
        this.imageUrl3 = dto.getImageUrl3();
        this.location = dto.getLocation();
        this.memoryDate = dto.getMemoryDate();
        this.modifyDate = LocalDateTime.now();
    }

}