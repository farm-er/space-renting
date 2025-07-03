package com.oussama.space_renting.service;

import com.oussama.space_renting.exception.BookingNotFoundException;
import com.oussama.space_renting.exception.StaffNotFoundException;
import com.oussama.space_renting.model.booking.Booking;
import com.oussama.space_renting.model.booking.BookingStatus;
import com.oussama.space_renting.repository.BookingRepository;
import com.oussama.space_renting.specification.BookingSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class BookingService  {


    public Page<Booking> findBookingsWithFilters(
            BookingStatus status,
            BigDecimal minTotal,
            BigDecimal maxTotal,
            UUID staffId,
            UUID spaceId,
            UUID renterId,
            Pageable pageable
    ) {


        Specification<Booking> spec = (root, query, cb) -> null;


        if (minTotal != null && maxTotal != null) {
            spec = spec.and(BookingSpecification.hasTotalAmountBetween(minTotal, maxTotal));
        }

        if (status != null) {
            spec = spec.and(BookingSpecification.hasStatus(status));
        }

        if (staffId != null) {
            spec = spec.and(BookingSpecification.isProcessedBy( staffId));
        }

        if (spaceId != null) {
            spec = spec.and(BookingSpecification.hasSpace( spaceId));
        }

        if (renterId != null) {
            spec = spec.and(BookingSpecification.hasRenter( renterId));
        }

        return bookingRepository.findAll(spec, pageable);
    }


    public Page<Booking> findBookingsByRenterId( UUID id, Pageable pageable) {
        return bookingRepository.findAll( BookingSpecification.hasRenter( id), pageable);
    }

    public Page<Booking> findPendingBookings( Pageable pageable) {
        return bookingRepository.findAll( BookingSpecification.hasStatus( BookingStatus.PENDING), pageable);
    }

    public Booking getBookingById( UUID id) {
        return bookingRepository.findById( id)
                .orElseThrow( () -> new BookingNotFoundException("Booking not found with id: " + id));
    }

    public Booking save( Booking booking) {
        return bookingRepository.save( booking);
    }


    /*
     * if this space is available now
     */
    public boolean existsBookings( UUID spaceId) {

        Specification<Booking> spec = (root, query, cb) -> null;

        LocalDateTime now = LocalDateTime.now();

        spec = spec.and( BookingSpecification.endAfter( now));
        spec = spec.and( BookingSpecification.startBefore( now));
        spec = spec.and( BookingSpecification.hasSpace( spaceId));

        return bookingRepository.exists( spec);
    }

    public boolean existsBookingsForSpaceRentedBy( UUID spaceId,  UUID renterId) {

        Specification<Booking> spec = BookingSpecification.hasRenter(renterId);

        spec = spec.and( BookingSpecification.hasSpace( spaceId));

        return bookingRepository.exists( spec);
    }

    /*
     * check overlapping bookings ofr a specific space
     */
    public boolean existOverlappingBookings( LocalDateTime start, LocalDateTime end, UUID spaceId) {

        Specification<Booking> specs = (root, query, cb) -> null;

        specs = specs
                .and(BookingSpecification.hasSpace(spaceId))
                .and(BookingSpecification.startBefore(start))
                .and(BookingSpecification.endAfter(end));

        return bookingRepository.exists( specs);
    }

    private final BookingRepository bookingRepository;

    public BookingService(
            BookingRepository bookingRepository
    ) {
        this.bookingRepository = bookingRepository;
    }
}
