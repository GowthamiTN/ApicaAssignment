package com.apica.usermangement.repository;

import com.apica.usermangement.model.Role;
import com.apica.usermangement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User>  findByEmail(String email);
}
