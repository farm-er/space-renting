package com.oussama.space_renting.service;


import com.oussama.space_renting.dto.staff.StaffDTO;
import com.oussama.space_renting.dto.staff.StaffRegisterRequestDTO;
import com.oussama.space_renting.exception.EmailAlreadyExistsException;
import com.oussama.space_renting.model.Staff.Staff;
import com.oussama.space_renting.repository.StaffRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class StaffServiceTest {

    @BeforeEach
    void setUp() {
        staffRepository = Mockito.mock(StaffRepository.class);
        staffService = new StaffService(staffRepository, passwordEncoder);
    }


    @DisplayName("Creating new staff throw exception when email is already used")
    @Test
    public void testCreateStaff_EmailAlreadyExists() {

        StaffRegisterRequestDTO staff = StaffRegisterRequestDTO.builder()
                .firstName("first name")
                .lastName("last name")
                .email("email@gmail.com")
                .password("password")
                .build();

        Mockito.when(staffRepository.existsByEmail(staff.getEmail())).thenReturn( true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> staffService.createStaff(staff),
                "Expected createStaff to throw but it didn't"
        );
    }

    @DisplayName("Creating new staff is completed successfully")
    @Test
    public void testCreateStaff_EmailDoesntExist() {

        StaffRegisterRequestDTO staff = StaffRegisterRequestDTO.builder()
                .firstName("first name")
                .lastName("last name")
                .email("email@gmail.com")
                .password("password")
                .build();


        Staff savedStaff = Staff.builder()
                .createdAt(LocalDateTime.now())
                .id(UUID.randomUUID())
                .firstName("first name")
                .lastName("last name")
                .email("email@gmail.com")
                .password("password")
                .build();

        Mockito.when(staffRepository.existsByEmail( staff.getEmail())).thenReturn( false);
        Mockito.when(staffRepository.save( any(Staff.class))).thenReturn( savedStaff);

        StaffDTO staffDTO = staffService.createStaff(staff);


        assertNotNull( staffDTO.getId());
        assertEquals( savedStaff.getId(), staffDTO.getId());
        assertEquals( savedStaff.getEmail(), staffDTO.getEmail());
        assertEquals( savedStaff.getFirstName(), staffDTO.getFirstName());
        assertEquals( savedStaff.getLastName(), staffDTO.getLastName());

    }

    @DisplayName("Should return staff with the provided id")
    @Test
    public void testGetStaffById_Found() {

        Staff staff = Staff.builder()
                .createdAt(LocalDateTime.now())
                .id(UUID.randomUUID())
                .firstName("first name")
                .lastName("last name")
                .email("email@gmail.com")
                .password("password")
                .build();

        Mockito.when(staffRepository.findById(staff.getId())).thenReturn(Optional.of(staff));

        Staff result = staffService.getStaffById(staff.getId());

        assertEquals( staff.getFirstName(), result.getFirstName());
        assertEquals( staff.getLastName(), result.getLastName());
        assertEquals( staff.getEmail(), result.getEmail());
    }

    @DisplayName("Should throw a Runtime Exception for staff not found by the respective id")
    @Test
    public void testGetStaffById_NotFound() {
        UUID id = UUID.randomUUID();
        Mockito.when(staffRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> staffService.getStaffById(id),
                "Expected getStaffById to throw, but it didn’t"
        );
        assertEquals("Staff not found with id: "+id.toString(), thrown.getMessage());
    }

    @DisplayName("Should return staff with the provided email")
    @Test
    public void testGetStaffByEmail_Found() {
        Staff staff = Staff.builder()
                .createdAt(LocalDateTime.now())
                .id(UUID.randomUUID())
                .firstName("first name")
                .lastName("last name")
                .email("email@gmail.com")
                .password("password")
                .build();

        Mockito.when(staffRepository.findByEmail(staff.getEmail())).thenReturn(Optional.of(staff));

        Staff result = staffService.getStaffByEmail(staff.getEmail());

        assertEquals( staff.getId(), result.getId());
        assertEquals( staff.getFirstName(), result.getFirstName());
        assertEquals( staff.getLastName(), result.getLastName());
        assertEquals( staff.getEmail(), result.getEmail());
    }

    @DisplayName("Should throw a Runtime Exception for staff not found by the respective email")
    @Test
    public void testGetStaffByEmail_NotFound() {
        UUID id = UUID.randomUUID();
        String email = "email@gmail.com";
        Mockito.when(staffRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> staffService.getStaffByEmail(email),
                "Expected getStaffByEmail to throw, but it didn’t"
        );
        assertEquals("Staff not found with email: " + email, thrown.getMessage());
    }

    @DisplayName("Should return true for the existing email")
    @Test
    public void testExistsByEmail_Exists() {
        String email = "email@gmail.com";
        Mockito.when( staffRepository.existsByEmail( email)).thenReturn( true);
        boolean result = staffService.existsByEmail( email);
        assertTrue( result);
    }

    @DisplayName("Should return false for the not existing email")
    @Test
    public void testExistsByEmail_NotExists() {
        String email = "email@gmail.com";
        Mockito.when( staffRepository.existsByEmail( email)).thenReturn( false);
        boolean result = staffService.existsByEmail( email);
        assertFalse( result);
    }


    private StaffRepository staffRepository;
    private StaffService staffService;

    @Mock
    private PasswordEncoder passwordEncoder;


}
