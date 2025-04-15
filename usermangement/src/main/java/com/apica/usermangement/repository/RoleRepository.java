package com.apica.usermangement.repository;

import com.apica.usermangement.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

    Set<Role> findByNameIn(Set<Role> roleNames);
}