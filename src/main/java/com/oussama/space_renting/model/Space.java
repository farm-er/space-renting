package com.oussama.space_renting.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "spaces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Space {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Size(max = 2000)
    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "space_type", nullable = false)
    private SpaceType spaceType;

    @NotNull
    @DecimalMin("0.0")
    @Column(name = "price_per_hour", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerHour;

    @Column(name = "price_per_day", precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    @Column(name = "price_per_month", precision = 10, scale = 2)
    private BigDecimal pricePerMonth;

    @DecimalMin("0.0")
    @Column(precision = 8, scale = 2)
    private BigDecimal area;

    @Column(nullable = false)
    private Integer capacity;

    @NotBlank
    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String state;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String country;

    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;

    @Builder.Default
    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "minimum_booking_hours")
    private Integer minimumBookingHours;

    @Column(name = "maximum_booking_hours")
    private Integer maximumBookingHours;

    @ElementCollection
    @CollectionTable(name = "space_amenities", joinColumns = @JoinColumn(name = "space_id"))
    @Column(name = "amenity")
    private List<String> amenities;

    @ElementCollection
    @CollectionTable(name = "space_images", joinColumns = @JoinColumn(name = "space_id"))
    @Column(name = "image_url")
    private List<String> imageUrls;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "space", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;

}