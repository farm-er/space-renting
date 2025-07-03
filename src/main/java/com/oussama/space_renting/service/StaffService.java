package com.oussama.space_renting.service;

import com.oussama.space_renting.dto.staff.StaffDTO;
import com.oussama.space_renting.dto.staff.StaffRegisterRequestDTO;
import com.oussama.space_renting.exception.EmailAlreadyExistsException;
import com.oussama.space_renting.exception.StaffNotFoundException;
import com.oussama.space_renting.model.Staff.Staff;
import com.oussama.space_renting.model.Staff.StaffRole;
import com.oussama.space_renting.model.Staff.StaffStatus;
import com.oussama.space_renting.model.User.User;
import com.oussama.space_renting.repository.StaffRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StaffService {

    public StaffDTO createStaff(StaffRegisterRequestDTO request)
        throws EmailAlreadyExistsException {
        // Business logic + validation
        if (staffRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        Staff staff = Staff.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(StaffRole.STAFF)
                .build();

        // I don't I need to handle these errors
        Staff savedStaff = staffRepository.save(staff);

        return StaffDTO.builder()
                .id(savedStaff.getId())
                .firstName(savedStaff.getFirstName())
                .lastName(savedStaff.getLastName())
                .email(savedStaff.getEmail())
                .role(savedStaff.getRole())
                .status(savedStaff.getStatus())
                .createdAt(savedStaff.getCreatedAt())
                .build();
    }

    public boolean existsByEmail( String email) {
        return staffRepository.existsByEmail(email);
    }

    public Page<Staff> getAllStaff(Pageable pageable) {
        return staffRepository.findAll( pageable);
    }

    public Staff getStaffById(UUID id) throws StaffNotFoundException{
        return staffRepository.findById(id)
                .orElseThrow(() -> new StaffNotFoundException("Staff not found with id: " + id));
    }

    public Staff getStaffByEmail(String email) throws StaffNotFoundException{
        return staffRepository.findByEmail(email)
                .orElseThrow(() -> new StaffNotFoundException("Staff not found with email: " + email));
    }

    public Staff save(Staff staff) {
        return staffRepository.save(staff);
    }

    public void delete( UUID id) {
        staffRepository.deleteById( id);
    }

    @Transactional
    public void updateStatus(UUID id, StaffStatus status) throws StaffNotFoundException {
        int result = staffRepository.updateStatus( id, status);
        if ( result != 1) {
            throw new StaffNotFoundException("Couldn't find staff with id: "+id);
        }
    }

    private final StaffRepository staffRepository;

    private final PasswordEncoder passwordEncoder;

    public StaffService(StaffRepository staffRepository, PasswordEncoder passwordEncoder) {
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
    }


}
