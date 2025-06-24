package com.chingubackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GroupMemoryRequest {
    @NotBlank
    private String title;

    private String content;

    @NotBlank
    private String imageUrl1;

    private String imageUrl2;
    private String imageUrl3;

    private String location;

    @NotNull
    private LocalDate memoryDate;
}
