package com.oussama.space_renting.security.custom_user_details;

import com.oussama.space_renting.model.Staff.Staff;
import com.oussama.space_renting.repository.StaffRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("staffDetailsService")
public class CustomStaffDetailsService implements UserDetailsService {

    private final StaffRepository staffRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Staff staff = staffRepository.findByEmail( email)
                .orElseThrow(() -> new UsernameNotFoundException("Staff not found"));


        switch (staff.getRole()) {
            case STAFF -> {
                return new org.springframework.security.core.userdetails.User(
                    staff.getEmail(),
                    staff.getPassword(),
                    List.of(new SimpleGrantedAuthority("ROLE_STAFF"))
                );
            }
            case MANAGER -> {
                return new org.springframework.security.core.userdetails.User(
                        staff.getEmail(),
                        staff.getPassword(),
                        List.of(new SimpleGrantedAuthority("ROLE_MANAGER"))
                );
            }
            default -> throw new UsernameNotFoundException("Invalid role for staff: " + email);
        }
    }

    public CustomStaffDetailsService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }
}
