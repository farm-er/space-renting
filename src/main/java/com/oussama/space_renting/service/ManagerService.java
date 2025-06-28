package com.oussama.space_renting.service;

import com.oussama.space_renting.model.Manager.Manager;
import com.oussama.space_renting.model.Staff.Staff;
import com.oussama.space_renting.repository.ManagerRepository;
import com.oussama.space_renting.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ManagerService {

    private final ManagerRepository managerRepository;


    public Manager getStaffById(UUID id) {
        return managerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + id.toString()));
    }

    public Manager getStaffByEmail(String email) {
        return managerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Staff not found with email: " + email));
    }

    public ManagerService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

}
