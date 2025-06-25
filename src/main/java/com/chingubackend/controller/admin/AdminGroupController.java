package com.chingubackend.controller.admin;

import com.chingubackend.dto.response.AdminGroupResponse;
import com.chingubackend.entity.Group;
import com.chingubackend.repository.GroupRepository;
import com.chingubackend.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin", description = "관리자 전용 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminGroupController {

    private final GroupRepository groupRepository;

    @GetMapping("/groups")
    @Operation(summary = "전체 그룹 및 멤버 목록 조회", description = "관리자가 전체 그룹 정보 및 구성원 목록을 확인할 수 있습니다.")
    public ResponseEntity<List<AdminGroupResponse>> getAllGroups(@AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("관리자 권한이 필요합니다.");
        }

        List<Group> groups = groupRepository.findAllWithMembersAndUsers();

        List<AdminGroupResponse> response = groups.stream()
                .map(AdminGroupResponse::new)
                .toList();

        return ResponseEntity.ok(response);
    }
}
