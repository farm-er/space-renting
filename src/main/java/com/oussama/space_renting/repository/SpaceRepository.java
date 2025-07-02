package com.oussama.space_renting.repository;


import com.oussama.space_renting.model.space.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface SpaceRepository extends JpaRepository<Space, UUID>, JpaSpecificationExecutor<Space> {

    /*
     * This repo use Specifications
     */

}
