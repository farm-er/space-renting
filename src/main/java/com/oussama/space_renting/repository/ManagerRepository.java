package com.oussama.space_renting.repository;


import com.oussama.space_renting.model.Manager.Manager;
import com.oussama.space_renting.model.User.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface ManagerRepository extends JpaRepository<Manager, UUID> {


    Optional<Manager> findByEmail(String email);

    boolean existsByEmail(String email);

}
