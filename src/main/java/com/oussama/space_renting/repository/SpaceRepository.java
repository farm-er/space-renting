package com.oussama.space_renting.repository;


import com.oussama.space_renting.model.space.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface SpaceRepository extends JpaRepository<Space, UUID>, JpaSpecificationExecutor<Space> {

    @Modifying
    @Query("UPDATE Space s SET s.isActive = :isActive WHERE s.id = :id")
    int updateIsActive(@Param("id") UUID id, @Param("isActive") boolean isActive);


}
