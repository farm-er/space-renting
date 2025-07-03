package com.oussama.space_renting.repository;

import com.oussama.space_renting.model.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;



@Repository
public interface ReviewsRepository extends JpaRepository<Review, UUID>, JpaSpecificationExecutor<Review> {


}