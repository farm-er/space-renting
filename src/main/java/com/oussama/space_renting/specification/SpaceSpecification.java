package com.oussama.space_renting.specification;

import com.oussama.space_renting.model.space.Amenity;
import com.oussama.space_renting.model.space.Space;
import com.oussama.space_renting.model.space.SpaceType;
import jakarta.persistence.metamodel.SetAttribute;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.Set;

// TODO: Add some null safety to those functions hahahahhah
public class SpaceSpecification {

    /*
     * For anything in an address
     */
    public static Specification<Space> hasAddressContaining(String address) {
        return (root, query, criteriaBuilder) -> {
            if (address == null || address.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(Space_.address)),
                    "%" + address.toLowerCase() + "%"
            );
        };
    }


    public static Specification<Space> hasAmenitiesContaining(Amenity amenity) {
        return (root, query, criteriaBuilder) -> {
            if (amenity == null || amenity.name().trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get(Space_.amenities).as(String.class)),
                    "%" + amenity.name().toLowerCase() + "%"
            );
        };
    }

    /*
     * Filter by the specified price range
     */
    public static Specification<Space> hasPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between( root.get(Space_.pricePerHour), minPrice, maxPrice);
    }

    public static Specification<Space> hasAreaBetween(BigDecimal minArea, BigDecimal maxArea) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between( root.get(Space_.area), minArea, maxArea);
    }

    public static Specification<Space> hasCapacityBetween(Integer minCapacity, Integer maxCapacity) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between( root.get(Space_.capacity), minCapacity, maxCapacity);
    }

    /*
     * Filter by city name (needs to be complete otherwise it doesn't match)
     */
    public static Specification<Space> hasCity(String city) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like( root.get(Space_.city), city);
    }

    public static Specification<Space> hasType(SpaceType type) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal( root.get(Space_.type), type);
    }

    public static Specification<Space> isAvailable() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue( root.get(Space_.isAvailable));
    }

    public static Specification<Space> isActive() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isTrue( root.get(Space_.isActive));
    }

    /*
     * Like cities' filter
     */
    public static Specification<Space> hasCountry(String country) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like( root.get(Space_.country), country);
    }

}


@StaticMetamodel(Space.class)
class Space_ {
    public static volatile SingularAttribute<Space, BigDecimal> pricePerHour;
    public static volatile SingularAttribute<Space, BigDecimal> area;
    public static volatile SingularAttribute<Space, Integer> capacity;
    public static volatile SingularAttribute<Space, String> city;
    public static volatile SingularAttribute<Space, SpaceType> type;
    public static volatile SingularAttribute<Space, String> country;
    public static volatile SingularAttribute<Space, String> address;
    public static volatile SingularAttribute<Space, Boolean> isAvailable;
    public static volatile SingularAttribute<Space, Boolean> isActive;
    public static volatile SetAttribute<Space, Set<Amenity>> amenities;
}




