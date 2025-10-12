package com.chingubackend.repository;

import com.chingubackend.entity.User;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(String userId);
    Optional<User> findByNickname(String nickname);
    Optional<User> findByNameAndEmail(String name, String email);
    Optional<User> findByUniqueKey(String uniqueKey);
    boolean existsByNickname(String nickname);
    List<User> findByNameContainingIgnoreCaseOrNicknameContainingIgnoreCase(
            String name, String nickname);
}
