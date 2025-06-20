package com.oussama.space_renting.dto;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(
        description = "Request body for creating a new space",
        requiredProperties = { "name", "pricePerDay" }
)
public class CreateSpaceDTO {

    @Schema(description = "Name of the space", example = "Garage A")
    private String name;

    @Schema(description = "Description of the space", example = "A large garage suitable for 2 cars")
    private String description;

    @Schema(description = "Price per day in USD", example = "50.0")
    private Double pricePerDay;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(Double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }
}
