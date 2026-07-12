package com.fincore.config;

import com.fincore.entity.Role;
import com.fincore.enums.RoleType;
import com.fincore.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    @Override
    public void run(String... args){
        if(!roleRepository.existsByRoleName(RoleType.ROLE_ADMIN)){
            Role adminRole = Role.builder()
                    .roleName(RoleType.ROLE_ADMIN)
                    .description("System Administrator")
                    .build();
            roleRepository.save(adminRole);

        }

        if(!roleRepository.existsByRoleName(RoleType.ROLE_CUSTOMER)){
            Role customerRole = Role.builder()
                    .roleName(RoleType.ROLE_CUSTOMER)
                    .description("Bank Customer")
                    .build();
            roleRepository.save(customerRole);
        }
    }
}
