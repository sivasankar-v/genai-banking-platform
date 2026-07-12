package com.fincore.repository;

import com.fincore.entity.Role;
import com.fincore.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByRoleName(RoleType roleName);
    boolean existsByRoleName(RoleType roleName);
}
