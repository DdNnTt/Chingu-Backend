package com.chingubackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GroupMemoryUpdateRequest {
    @NotBlank
    private String title;

    private String content;

    private String imageUrl1;
    private String imageUrl2;
    private String imageUrl3;

    private String location;

    @NotNull
    private LocalDate memoryDate;
}
