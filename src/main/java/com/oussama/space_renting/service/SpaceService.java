package com.oussama.space_renting.service;

import com.oussama.space_renting.model.space.Amenity;
import com.oussama.space_renting.model.space.Space;
import com.oussama.space_renting.model.space.SpaceType;
import com.oussama.space_renting.repository.SpaceRepository;
import com.oussama.space_renting.specification.SpaceSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

public class SpaceService {

    @Autowired
    private SpaceRepository spaceRepository;


    public List<Space> findByIsActive() {
        return spaceRepository.findAll(SpaceSpecification.isActive());
    }


    public Page<Space> findSpacesWithFilters(
            String address,
            Amenity amenity,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            BigDecimal minArea,
            BigDecimal maxArea,
            Integer minCapacity,
            Integer maxCapacity,
            String city,
            String country,
            SpaceType type,
            Boolean availableOnly,
            Pageable pageable) {

        /*
         * Had to do something to initialize the specs
         */
        Specification<Space> spec = (root, query, cb) -> null;

        if (address != null && !address.trim().isEmpty()) {
            spec = spec.and(SpaceSpecification.hasAddressContaining(address));
        }

        if (amenity != null) {
            spec = spec.and(SpaceSpecification.hasAmenitiesContaining(amenity));
        }

        if (minPrice != null && maxPrice != null) {
            spec = spec.and(SpaceSpecification.hasPriceBetween(minPrice, maxPrice));
        }

        if (minArea != null && maxArea != null) {
            spec = spec.and(SpaceSpecification.hasAreaBetween(minArea, maxArea));
        }

        if (minCapacity != null && maxCapacity != null) {
            spec = spec.and(SpaceSpecification.hasCapacityBetween(minCapacity, maxCapacity));
        }

        if (city != null && !city.trim().isEmpty()) {
            spec = spec.and(SpaceSpecification.hasCity(city));
        }

        if (country != null && !country.trim().isEmpty()) {
            spec = spec.and(SpaceSpecification.hasCountry(country));
        }

        if (type != null) {
            spec = spec.and(SpaceSpecification.hasType(type));
        }

        if (availableOnly != null && availableOnly) {
            spec = spec.and(SpaceSpecification.isAvailable());
        }
        return spaceRepository.findAll(spec, pageable);
    }
}
