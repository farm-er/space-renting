package com.oussama.space_renting.service;

import com.oussama.space_renting.model.Staff.Staff;
import com.oussama.space_renting.model.User.User;
import com.oussama.space_renting.repository.StaffRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StaffService {


    private final StaffRepository staffRepository;


    public Staff getStaffById(UUID id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found with id: " + id.toString()));
    }

    public Staff getStaffByEmail(String email) {
        return staffRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Staff not found with email: " + email));
    }

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }


}
