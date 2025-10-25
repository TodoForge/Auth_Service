package com.auth.Auth.Service.config;

import com.auth.Auth.Service.entity.Role;
import com.auth.Auth.Service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {

        if(!roleRepository.findByName("ADMIN").isPresent()){
            Role role = new Role();
            role.setName("ADMIN");
            role.setDescription("Administrator Role");
            roleRepository.save(role);
        }

        if(!roleRepository.findByName("USER").isPresent()){
            Role role = new Role();
            role.setName("USER");
            role.setDescription("User Role");
            roleRepository.save(role);
        }
        System.out.println("Role Saved");
    }
}
