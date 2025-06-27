package com.oussama.space_renting.model;

import com.oussama.space_renting.model.space.Space;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


/*
 * I removed the boilerplate and replaced it with lombok annotations
 */
/*
 * Represents a user
 * This User can own spaces, book spaces
 * This User can also post reviews on for spaces
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    /*
     * Unique identifier
     * Using UUID with GeneratedValue which supposed to work out of the box with UUID
     */
    @Id
    @GeneratedValue
    private UUID id;

    /*
     * first name of the user
     */
    @NotBlank
    @Size(max = 100)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    /*
     * last name of the user
     */
    @NotBlank
    @Size(max = 100)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    /*
     * Unique email of the user
     */
    @Email( message= "invalid email", regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    /*
     * User's account password
     */
    @NotBlank
    @Column(nullable = false)
    private String password;

    /*
     * User's phone number
     */
    @Size(max = 20)
    @Column( name = "phone_number", unique= true, nullable = false)
    private String phoneNumber;

    /*
     * User role can be Renter or Owner
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    /*
     * is the account verified
     */
    @Builder.Default
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    /*
     * is the user active
     */
    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /*
     * Account creation time
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /*
     * Last time the user updated the account
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /*
     * Bookings done by this user
     */
    @OneToMany(mappedBy = "renter", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    /*
     * Reviews posted by the user
     */
    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviewsGiven;

}