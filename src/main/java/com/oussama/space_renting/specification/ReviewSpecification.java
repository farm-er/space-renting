package com.oussama.space_renting.specification;


import com.oussama.space_renting.model.booking.Booking;
import com.oussama.space_renting.model.booking.BookingStatus;
import com.oussama.space_renting.model.booking.Booking_;
import com.oussama.space_renting.model.review.Review;
import com.oussama.space_renting.model.review.Review_;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;


public class ReviewSpecification {

    public static Specification<Review> hasRatingBetween(Integer minRating, Integer maxRating) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between( root.get(Review_.rating), minRating, maxRating);
    }

    public static Specification<Review> hasRatingLessThan(Integer maxRating) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThan( root.get( Review_.rating), maxRating);
    }

    public static Specification<Review> hasRatingGreaterThan(Integer minRating) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan( root.get( Review_.rating), minRating);
    }

    public static Specification<Review> hasSpace(UUID spaceId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal( root.get(Review_.spaceId), spaceId);
    }

    public static Specification<Review> hasReviewer(UUID reviewerId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal( root.get(Review_.reviewerId), reviewerId);
    }

}
