package com.chingubackend.dto.request;

import com.chingubackend.model.RequestStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupInviteStatusRequest {
    @NotNull(message = "요청 상태는 필수입니다.")
    @Schema(description = "요청 상태 (ACCEPTED 또는 REJECTED)", example = "ACCEPTED")
    private RequestStatus status;
}