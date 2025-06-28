package com.oussama.space_renting.repository;


import com.oussama.space_renting.model.space.Amenity;
import com.oussama.space_renting.model.space.Space;
import com.oussama.space_renting.model.space.SpaceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long>, JpaSpecificationExecutor<Space> {

    /*
     * This repo use Specifications
     */

}
