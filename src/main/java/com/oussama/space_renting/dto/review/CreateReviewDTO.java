package com.oussama.space_renting.dto.review;


import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;


@Builder
@Getter
@AllArgsConstructor
public class CreateReviewDTO {

    @Schema(description = "ID of the space to review",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Space ID is required")
    @JsonProperty("space_id")
    private UUID spaceId;


    @Schema(description = "Comment of the reviewer",
            example = "Good space in my opinion",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "comment is required")
    private String comment;

    @Schema(description = "Rating of the reviewer",
            example = "4",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 0, message = "minimum rating is 1")
    @Max(value = 5, message = "maximum rating is 5")
    @NotNull(message = "rating is required")
    private Integer rating;

}
