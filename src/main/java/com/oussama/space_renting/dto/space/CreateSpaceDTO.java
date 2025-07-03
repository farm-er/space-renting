package com.oussama.space_renting.dto.space;

import com.oussama.space_renting.model.space.Amenity;
import com.oussama.space_renting.model.space.SpaceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;


@Getter
@AllArgsConstructor
@Builder
@Schema(description = "Data Transfer Object for creating a new space")
public class CreateSpaceDTO {

    @NotBlank(message = "Space name is required")
    @Size(min = 2, max = 100, message = "Space name must be between 2 and 100 characters")
    @Schema(description = "Name of the space", example = "Downtown Conference Room A")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    @Schema(description = "Detailed description of the space",
            example = "A modern conference room with natural lighting, suitable for meetings and presentations"
    )
    private String description;

    @NotNull(message = "Price per hour is required")
    @DecimalMin(value = "0.01", message = "Price per hour must be greater than 0")
    @DecimalMax(value = "10000.00", message = "Price per hour cannot exceed 10,000")
    @Digits(integer = 6, fraction = 2, message = "Price must have at most 6 digits before decimal and 2 after")
    @Schema(description = "Price per hour in USD", example = "25.50")
    private BigDecimal pricePerHour;

    @NotNull(message = "Space type is required")
    @Schema(description = "Type of the space", example = "OFFICE")
    private SpaceType type;

    @DecimalMin(value = "0.00", message = "Discount cannot be negative")
    @DecimalMax(value = "100.00", message = "Discount cannot exceed 100%")
    @Digits(integer = 3, fraction = 2, message = "Discount must have at most 3 digits before decimal and 2 after")
    @Builder.Default
    @Schema(description = "Discount percentage (0-100)", example = "10.50")
    private BigDecimal discount = BigDecimal.ZERO;

    @NotNull(message = "Area is required")
    @DecimalMin(value = "0.1", message = "Area must be greater than 0.1 square meters")
    @DecimalMax(value = "100000.00", message = "Area cannot exceed 100,000 square meters")
    @Digits(integer = 8, fraction = 2, message = "Area must have at most 8 digits before decimal and 2 after")
    @Schema(description = "Area of the space in square meters", example = "45.75")
    private BigDecimal area;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1 person")
    @Max(value = 10000, message = "Capacity cannot exceed 10,000 people")
    @Schema(description = "Maximum number of people the space can accommodate", example = "12")
    private Integer capacity;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters")
    @Schema(description = "Street address of the space",
            example = "123Boulevard MohammedV,Casablanca")
    private String address;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 50, message = "City name must be between 2 and 50 characters")
    @Schema(description = "City where the space is located", example = "Casablanca")
    private String city;

    @Pattern(regexp = "^[0-9]{5}(-[0-9]{4})?$|^[A-Z0-9]{3,10}$",
            message = "Invalid postal code format")
    @Builder.Default
    @Schema(description = "Postal code", example = "20000")
    private String postalCode = null;

    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 50, message = "Country name must be between 2 and 50 characters")
    @Schema(description = "Country where the space is located", example = "Morocco")
    private String country;

    @DecimalMin(value = "-90.0", message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90.0", message = "Latitude must be between -90 and 90")
    @Digits(integer = 2, fraction = 8, message = "Latitude precision too high")
    @Schema(description = "Latitude coordinate", example = "33.5731")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0", message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0", message = "Longitude must be between -180 and 180")
    @Digits(integer = 3, fraction = 8, message = "Longitude precision too high")
    @Schema(description = "Longitude coordinate", example = "-7.5898")
    private BigDecimal longitude;

    @Size(max = 20, message = "Cannot have more than 20 amenities")
    @Builder.Default
    @Schema(description = "Set of amenities available in the space",
            example = "[\"HIGH_SPEED_WIFI\", \"ERGONOMIC_CHAIRS\", \"STANDING_DESKS\"]")
    private Set<Amenity> amenities = Set.of();


    @Size(max = 10, message = "Cannot have more than 10 images")
    @Builder.Default
    @Schema(description = "List of image URLs for the space")
    private List<String> imageUrls = List.of();

}
