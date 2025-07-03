package com.oussama.space_renting.controller.staff;


import com.oussama.space_renting.dto.staff.StaffDTO;
import com.oussama.space_renting.dto.user.UserDTO;
import com.oussama.space_renting.exception.StaffNotFoundException;
import com.oussama.space_renting.exception.UserNotFoundException;
import com.oussama.space_renting.model.Staff.Staff;
import com.oussama.space_renting.model.Staff.StaffRole;
import com.oussama.space_renting.model.Staff.StaffStatus;
import com.oussama.space_renting.model.User.User;
import com.oussama.space_renting.service.StaffService;
import com.oussama.space_renting.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/staff")
@Tag(name = "Staff", description = "Operations for managing staff")
public class StaffController {

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping
    public ResponseEntity<?> getAllStaff(
            @Parameter(description = "Page number (0...N)")
            @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Number of items per page")
            @RequestParam(required = false, defaultValue = "10") int size,

            @Parameter(description = "Sort direction (asc or desc)")
            @RequestParam(required = false, defaultValue = "desc") String sortDir
    ) {

        Sort sort = Sort.by(
                sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                "createdAt"
        );

        // Create pageable object
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Staff> staffTeam = staffService.getAllStaff( pageable);

        return ResponseEntity.ok(staffTeam);
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('STAFF')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getStaff(@PathVariable UUID id, Authentication authentication) {

        try {

            Staff staff = staffService.getStaffById( id);

            StaffDTO staffDTO = StaffDTO.builder()
                    .id(staff.getId())
                    .email(staff.getEmail())
                    .firstName(staff.getFirstName())
                    .lastName(staff.getLastName())
                    .createdAt(staff.getCreatedAt())
                    .updatedAt(staff.getUpdatedAt())
                    .build();

            return ResponseEntity.ok(staffDTO);

        } catch ( IllegalArgumentException e ) {
            return ResponseEntity.badRequest()
                    .body("Missing id");
        } catch ( StaffNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStaff(
            @PathVariable UUID id,
            @RequestBody Map<String, String> updates,
            Authentication authentication
    ) {

        try {

            Staff staff = staffService.getStaffById( id);


            updates.forEach((key, value) -> {
                switch (key) {
                    case "firstName" -> staff.setFirstName( value);
                    case "lastName" -> staff.setLastName( value);
                    case "email" -> staff.setEmail( value);
                }
            });

            Staff savedStaff = staffService.save(staff);

            StaffDTO staffDTO = StaffDTO.builder()
                    .id(savedStaff.getId())
                    .email(savedStaff.getEmail())
                    .firstName(savedStaff.getFirstName())
                    .lastName(savedStaff.getLastName())
                    .createdAt(savedStaff.getCreatedAt())
                    .updatedAt(savedStaff.getUpdatedAt())
                    .build();

            return ResponseEntity.ok(staffDTO);

        } catch ( IllegalArgumentException e ) {
            return ResponseEntity.badRequest()
                    .body("Missing id");
        } catch ( StaffNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }

    }

    @PreAuthorize("hasRole('MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStaff(
            @PathVariable UUID id,
            @RequestBody Map<String, String> updates,
            Authentication authentication
    ) {

        try {

            Staff staff = staffService.getStaffById( id);

            if (staff.getRole() == StaffRole.MANAGER) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Can't delete manager account");
            }

            staffService.delete( staff.getId());

            return ResponseEntity.ok("Staff deleted successfully");

        } catch ( IllegalArgumentException e ) {
            return ResponseEntity.badRequest()
                    .body("Missing id");
        } catch ( StaffNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }

    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/suspend/{id}")
    public ResponseEntity<?> suspendStaff(
            @PathVariable UUID id,
            Authentication authentication
    ) {

        try {

            Staff staff = staffService.getStaffById( id);

            if (staff.getRole() == StaffRole.MANAGER) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Can't suspend manager account");
            }

            staffService.updateStatus( staff.getId(), StaffStatus.SUSPENDED);

            return ResponseEntity.ok("Staff suspended successfully");

        } catch ( IllegalArgumentException e ) {
            return ResponseEntity.badRequest()
                    .body("Missing id");
        } catch ( StaffNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }


    }


    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/activate/{id}")
    public ResponseEntity<?> activateStaff(
            @PathVariable UUID id,
            Authentication authentication
    ) {

        try {

            Staff staff = staffService.getStaffById( id);

            if (staff.getRole() == StaffRole.MANAGER) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Can't suspend manager account");
            }

            staffService.updateStatus( staff.getId(), StaffStatus.ACTIVE);

            return ResponseEntity.ok("Staff suspended successfully");

        } catch ( IllegalArgumentException e ) {
            return ResponseEntity.badRequest()
                    .body("Missing id");
        } catch ( StaffNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }


    }


    private final StaffService staffService;

    public StaffController(
            StaffService staffService
    ) {
        this.staffService = staffService;
    }

}
