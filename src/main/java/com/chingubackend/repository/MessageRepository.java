package com.chingubackend.repository;

import com.chingubackend.entity.Message;
import com.chingubackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message,Long> {
    Long countByReceiverIdAndReadStatus(Long receiverId, boolean readStatus);
    List<Message> findAllByReceiverOrderBySendTimeDesc(User receiver);
}