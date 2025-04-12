package com.chingubackend.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupInviteRequest {
    private List<Long> friendUserIds;
}