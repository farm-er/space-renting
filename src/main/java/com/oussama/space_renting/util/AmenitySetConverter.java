package com.oussama.space_renting.util;

import com.oussama.space_renting.model.space.Amenity;
import jakarta.persistence.AttributeConverter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;



/*
 * Converter for amenities to db column
 */
public class AmenitySetConverter implements AttributeConverter<Set<Amenity>, String>  {

    private static final String SPLIT_CHAR = ";";

    @Override
    public String convertToDatabaseColumn(Set<Amenity> amenities) {
        if (amenities == null || amenities.isEmpty()) {
            return null;
        }
        return amenities.stream()
                .map(Enum::name)
                .collect(Collectors.joining(SPLIT_CHAR));
    }


    @Override
    public Set<Amenity> convertToEntityAttribute(String amenities) {
        if (amenities == null || amenities.trim().isEmpty()) {
            return new HashSet<>();
        }

        return Arrays.stream(amenities.split(SPLIT_CHAR))
                .map(Amenity::valueOf)
                .collect(Collectors.toSet());
    }

}
