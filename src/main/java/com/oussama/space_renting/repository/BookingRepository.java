package com.oussama.space_renting.repository;

import com.oussama.space_renting.dto.analytics.RevenuePerDayDTO;
import com.oussama.space_renting.model.booking.Booking;
import com.oussama.space_renting.model.booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

//    @Query("SELECT new com.oussama.space_renting.dto.RevenuePerDayDTO(CAST(b.processedAt AS date), SUM(b.totalAmount)) " +
//            "FROM Booking b " +
//            "WHERE b.status = :status " +
//            "AND b.processedAt BETWEEN :start AND :end " +
//            "GROUP BY CAST(b.processedAt AS date) " +
//            "ORDER BY CAST(b.processedAt AS date)")
//    List<RevenuePerDayDTO> findDailyRevenueBetween(
//            @Param("start") LocalDateTime start,
//            @Param("end") LocalDateTime end,
//            @Param("status") BookingStatus status
//    );


    @Query("SELECT DATE(b.processedAt) as date, SUM(b.totalAmount) as total " +
            "FROM Booking b " +
            "WHERE b.status = :status " +
            "AND b.processedAt BETWEEN :start AND :end " +
            "GROUP BY DATE(b.processedAt) " +
            "ORDER BY DATE(b.processedAt)")
    List<RevenuePerDayDTO> findDailyRevenueBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("status") BookingStatus status
    );

}
