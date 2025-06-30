package com.oussama.space_renting.repository;

import com.oussama.space_renting.model.Staff.Staff;
import com.oussama.space_renting.model.Staff.StaffRole;
import com.oussama.space_renting.model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {

    Optional<Staff>  findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRole(StaffRole role);

}
