package com.oussama.space_renting.dto.booking;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "Booking creation request")
public class CreateBookingDTO {

    @Schema(description = "ID of the space to book",
            example = "550e8400-e29b-41d4-a716-446655440000",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Space ID is required")
    @JsonProperty("space_id")
    private UUID spaceId;

    @Schema(description = "Booking start date and time",
            example = "2025-07-05T10:00:00",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("start_time")
    private LocalDateTime startTime;

    @Schema(description = "Booking end date and time",
            example = "2025-07-05T14:00:00",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @JsonProperty("end_time")
    private LocalDateTime endTime;



    @AssertTrue(message = "End time must be after start time")
    @Schema(hidden = true)
    public boolean isValidTimeRange() {
        if (startTime == null || endTime == null) {
            return true;
        }
        return endTime.isAfter(startTime);
    }

    @AssertTrue(message = "Booking duration must be at least 1 hour")
    @Schema(hidden = true)
    public boolean isValidDuration() {
        if (startTime == null || endTime == null) {
            return true;
        }
        return java.time.Duration.between(startTime, endTime).toHours() >= 1;
    }

}
