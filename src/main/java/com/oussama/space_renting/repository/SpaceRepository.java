package com.oussama.space_renting.repository;


import com.oussama.space_renting.model.space.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SpaceRepository extends JpaRepository<Space, Long>, JpaSpecificationExecutor<Space> {
    // JpaSpecificationExecutor provides:
    // - findAll(Specification<Space> spec)
    // - findOne(Specification<Space> spec)
    // - count(Specification<Space> spec)
    // - etc.
}
