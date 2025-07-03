package com.oussama.space_renting.dto.booking;

import com.oussama.space_renting.model.Staff.Staff;
import com.oussama.space_renting.model.booking.BookingStatus;
import com.oussama.space_renting.model.space.Space;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Builder
@Getter
@AllArgsConstructor
public class BookingDTO {
    private UUID id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalAmount;
    private BookingStatus status;
    private String cancellationReason;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
    private LocalDateTime cancelledAt;
    private UUID ProcessedById;
    private UUID spaceId;
    private UUID renterId;
}
