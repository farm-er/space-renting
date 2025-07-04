package com.oussama.space_renting.repository;

import com.oussama.space_renting.model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /*
     * Find user by Email
     */
    Optional<User> findByEmail(String email);

    /*
     * Used to check if the email is already used (already exists)
     */
    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt BETWEEN :start AND :end")
    int countAccountCreatedBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


}
