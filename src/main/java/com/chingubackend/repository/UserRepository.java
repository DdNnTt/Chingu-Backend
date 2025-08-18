package com.chingubackend.repository;

import com.chingubackend.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
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
    List<User> findByNameOrNicknameOrUserId(String name, String nickname, String userId);

    // 관리자 회원 검색 시
    @Query("SELECT u FROM User u WHERE u.name LIKE %:keyword% OR u.nickname LIKE %:keyword% OR u.userId LIKE %:keyword%")
    List<User> searchByKeyword(@Param("keyword") String keyword);
}
