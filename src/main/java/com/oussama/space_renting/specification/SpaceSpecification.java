package com.oussama.space_renting.specification;

import com.oussama.space_renting.model.booking.Booking;
import com.oussama.space_renting.model.booking.Booking_;
import com.oussama.space_renting.model.space.Amenity;
import com.oussama.space_renting.model.space.Space;
import com.oussama.space_renting.model.space.SpaceType;
import com.oussama.space_renting.model.space.Space_;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// TODO: Add some null safety to those functions hahahahhah
public class SpaceSpecification {

    /*
     * For anything in an address
     */
    public static Specification<Space> hasAddressContaining(String address) {
        return (root, query, criteriaBuilder) -> {
            if (address == null || address.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(Space_.address)),
                    "%" + address.toLowerCase() + "%"
            );
        };
    }


    public static Specification<Space> hasAmenitiesContaining(Amenity amenity) {
        return (root, query, criteriaBuilder) -> {
            if (amenity == null || amenity.name().trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(Space_.amenities).as(String.class)),
                    "%" + amenity.name().toLowerCase() + "%"
            );
        };
    }

    /*
     * Filter by the specified price range
     */
    public static Specification<Space> hasPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between( root.get(Space_.pricePerHour), minPrice, maxPrice);
    }

    public static Specification<Space> hasAreaBetween(BigDecimal minArea, BigDecimal maxArea) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between( root.get(Space_.area), minArea, maxArea);
    }

    public static Specification<Space> hasCapacityBetween(Integer minCapacity, Integer maxCapacity) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between( root.get(Space_.capacity), minCapacity, maxCapacity);
    }

    /*
     * Filter by city name (needs to be complete otherwise it doesn't match)
     */
    public static Specification<Space> hasCity(String city) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like( root.get(Space_.city), city);
    }

    public static Specification<Space> hasType(SpaceType type) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal( root.get(Space_.spaceType), type);
    }

    public static Specification<Space> isAvailable() {
        return (root, query, criteriaBuilder) -> {

            LocalDateTime now = LocalDateTime.now();

            Subquery<UUID> subquery = query.subquery(UUID.class);
            Root<Booking> bookingRoot = subquery.from(Booking.class);

            subquery.select( bookingRoot.get(Booking_.spaceId))
                            .where(
                                    criteriaBuilder.lessThan( bookingRoot.get(Booking_.startTime), now),
                                    criteriaBuilder.greaterThan( bookingRoot.get(Booking_.endTime), now)
                            );

            return criteriaBuilder.not( root.get( Space_.id).in( subquery));
        };
    }

    public static Specification<Space> isActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue( root.get(Space_.isActive));
    }

    /*
     * Like cities' filter
     */
    public static Specification<Space> hasCountry(String country) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like( root.get(Space_.country), country);
    }

}




