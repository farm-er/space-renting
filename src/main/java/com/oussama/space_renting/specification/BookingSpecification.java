package com.oussama.space_renting.specification;


import com.oussama.space_renting.model.booking.Booking_;
import com.oussama.space_renting.model.booking.Booking;
import com.oussama.space_renting.model.booking.BookingStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class BookingSpecification {

    public static Specification<Booking> hasStatus( BookingStatus status) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal( root.get(Booking_.status), status);
    }

    public static Specification<Booking> hasTotalAmountBetween( BigDecimal minTotal, BigDecimal maxTotal) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between( root.get( Booking_.totalAmount), minTotal, maxTotal);
    }

    public static Specification<Booking> startBefore(LocalDateTime start) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan( root.get( Booking_.endTime), start);
    }

    public static Specification<Booking> endAfter(LocalDateTime end) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThan( root.get( Booking_.startTime), end);
    }

    public static Specification<Booking> isProcessedBy(UUID staffId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get( Booking_.processedBy).get("id"), staffId);
    }

    public static Specification<Booking> hasSpace(UUID spaceId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get( Booking_.space).get("id"), spaceId);
    }

    public static Specification<Booking> hasRenter(UUID renterId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get( Booking_.renter).get("id"), renterId);
    }

}


