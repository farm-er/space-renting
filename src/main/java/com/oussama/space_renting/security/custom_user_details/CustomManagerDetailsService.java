package com.oussama.space_renting.security.custom_user_details;

import com.oussama.space_renting.model.Manager.Manager;
import com.oussama.space_renting.repository.ManagerRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("managerDetailsService")
public class CustomManagerDetailsService implements UserDetailsService {

    private final ManagerRepository managerRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Manager manager = managerRepository.findByEmail( email)
                .orElseThrow(() -> new UsernameNotFoundException("Manager not found"));

        return new org.springframework.security.core.userdetails.User(
                manager.getEmail(),
                manager.getPassword(),
                List.of(new SimpleGrantedAuthority("MANAGER"))
        );
    }

    public CustomManagerDetailsService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }
}
