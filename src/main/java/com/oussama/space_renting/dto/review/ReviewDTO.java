package com.oussama.space_renting.dto.review;


import com.oussama.space_renting.model.User.User;
import com.oussama.space_renting.model.space.Space;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class ReviewDTO {

    private UUID id;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;

    private UUID spaceId;

    private UUID reviewerId;

}
