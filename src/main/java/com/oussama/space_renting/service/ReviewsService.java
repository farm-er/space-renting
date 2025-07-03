package com.oussama.space_renting.service;


import com.oussama.space_renting.model.booking.Booking;
import com.oussama.space_renting.model.review.Review;
import com.oussama.space_renting.repository.ReviewsRepository;
import com.oussama.space_renting.specification.ReviewSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReviewsService {


    public Page<Review> findReviewsWithRatingRange(
            Integer minRating,
            Integer maxRating,
            Pageable pageable
    ){

        Specification<Review> spec = (root, query, cb) -> null;

        if (minRating != null && maxRating != null) {
            spec = spec.and(ReviewSpecification.hasRatingBetween( minRating, maxRating));
        } else if (minRating != null) {
            spec = spec.and( ReviewSpecification.hasRatingGreaterThan(minRating));
        } else if ( maxRating != null) {
            spec = spec.and(ReviewSpecification.hasRatingLessThan(maxRating));
        }

        return reviewsRepository.findAll( spec, pageable);
    }

    public Page<Review> findReviewsBySpace(
            UUID spaceId,
            Pageable pageable
    ) {

        Specification<Review> spec = ReviewSpecification.hasSpace( spaceId);

        return reviewsRepository.findAll( spec, pageable);
    }


    public boolean reviewedSpaceBefore(
            UUID spaceId,
            UUID reviewerId
    ) {

        Specification<Review> spec = ReviewSpecification.hasSpace( spaceId);

        spec = spec.and( ReviewSpecification.hasReviewer( reviewerId));

         return reviewsRepository.exists( spec);
    }

    public Review save( Review review) {
        return reviewsRepository.save( review);
    }


    private final ReviewsRepository reviewsRepository;

    public ReviewsService( ReviewsRepository reviewsRepository) {
        this.reviewsRepository = reviewsRepository;
    }

}
