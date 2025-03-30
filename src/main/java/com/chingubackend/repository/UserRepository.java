package com.chingubackend.repository;

import com.chingubackend.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(String userId);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByNameAndEmail(String name, String email);
    void deleteByUserId(String userId);
    List<User> findByNameOrNicknameOrUserId(String name, String nickname, String userId);
}
