package com.oussama.space_renting.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;



/**
 * Represents a user in the renting platform
 * This User can own spaces, book spaces
 * This User can also post reviews on for spaces
 */
@Entity
@Table(name = "users")
public class User {
    /*
     * Unique identifier for the user incremented by one
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    @Email
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
    @Column(name = "phone_number")
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
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    /*
     * is the user active
     */
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
     * Owned spaces
     */
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Space> ownedSpaces;

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

    // Constructors
    public User() {}

    public User(String firstName, String lastName, String email, String password, UserRole role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public Boolean getIsVerified() { return isVerified; }
    public void setIsVerified(Boolean isVerified) { this.isVerified = isVerified; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Space> getOwnedSpaces() { return ownedSpaces; }
    public void setOwnedSpaces(List<Space> ownedSpaces) { this.ownedSpaces = ownedSpaces; }

    public List<Booking> getBookings() { return bookings; }
    public void setBookings(List<Booking> bookings) { this.bookings = bookings; }

    public List<Review> getReviewsGiven() { return reviewsGiven; }
    public void setReviewsGiven(List<Review> reviewsGiven) { this.reviewsGiven = reviewsGiven; }
}