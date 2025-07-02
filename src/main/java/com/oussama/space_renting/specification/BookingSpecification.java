package com.oussama.space_renting.specification;


import com.oussama.space_renting.model.Staff.Staff;
import com.oussama.space_renting.model.booking.Booking;
import com.oussama.space_renting.model.booking.BookingStatus;
import com.oussama.space_renting.model.space.Space;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
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

    public static Specification<Booking> isProcessedBy(UUID staffId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get( Booking_.processedBy).get("id"), staffId);
    }

    public static Specification<Booking> hasSpace(UUID spaceId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get( Booking_.space).get("id"), spaceId);
    }

}


@StaticMetamodel(Booking.class)
class Booking_ {
    public static volatile SingularAttribute<Booking, BigDecimal> totalAmount;
    public static volatile SingularAttribute<Booking, BookingStatus> status;
    public static volatile SingularAttribute<Booking, Staff> processedBy;
    public static volatile SingularAttribute<Booking, Space> space;
}