package com.fincore.config;

import com.fincore.entity.Role;
import com.fincore.entity.User;
import com.fincore.enums.RoleType;
import com.fincore.enums.UserStatus;
import com.fincore.repository.RoleRepository;
import com.fincore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        Role adminRole = ensureRole(RoleType.ROLE_ADMIN, "System Administrator");
        ensureRole(RoleType.ROLE_CUSTOMER, "Bank Customer");
        createAdminIfConfigured(adminRole);
    }

    private Role ensureRole(RoleType type, String description) {
        return roleRepository.findByRoleName(type).orElseGet(() ->
                roleRepository.save(Role.builder().roleName(type).description(description).build()));
    }

    // Registration only ever creates customers, so the first admin comes from configuration.
    private void createAdminIfConfigured(Role adminRole) {
        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            return;
        }
        String email = adminEmail.toLowerCase().trim();
        if (userRepository.existsByEmail(email)) {
            return;
        }
        userRepository.save(User.builder()
                .firstName("System")
                .lastName("Admin")
                .email(email)
                .password(passwordEncoder.encode(adminPassword))
                .mobileNumber("0000000000")
                .status(UserStatus.ACTIVE)
                .role(adminRole)
                .build());
        log.info("Created admin user {}", email);
    }
}
