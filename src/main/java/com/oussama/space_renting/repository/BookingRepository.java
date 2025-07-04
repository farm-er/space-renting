package com.oussama.space_renting.repository;

import com.oussama.space_renting.model.booking.Booking;
import com.oussama.space_renting.model.booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID>, JpaSpecificationExecutor<Booking> {

    @Query("SELECT COUNT(DISTINCT b.renterId) FROM Booking b WHERE b.status = :status AND b.processedAt BETWEEN :start AND :end")
    int countDistinctUsersByProcessedAtBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("status") BookingStatus status
    );

    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.status = :status AND b.processedAt BETWEEN :start AND :end")
    BigDecimal sumRevenueBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("status") BookingStatus status
    );

    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.status = :status")
    BigDecimal sumTotalRevenue(@Param("status") BookingStatus status);

}
