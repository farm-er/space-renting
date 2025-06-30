package com.oussama.space_renting.config;

import com.oussama.space_renting.model.Staff.Staff;
import com.oussama.space_renting.model.Staff.StaffRole;
import com.oussama.space_renting.repository.StaffRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initializeDefaultManager() {
        // Check if any manager exists
        if (!staffRepository.existsByRole(StaffRole.MANAGER)) {
            Staff defaultManager = Staff.builder()
                    .firstName("Admin")
                    .lastName("Manager")
                    .email("admin@company.com")
                    .password(passwordEncoder.encode("12345678"))
                    .role(StaffRole.MANAGER)
                    .build();

            staffRepository.save(defaultManager);
            System.out.println("Default manager created: admin@company.com and " + defaultManager.getPassword());
        }
    }
}
