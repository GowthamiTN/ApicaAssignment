package com.apica.usermangement.dto;
import com.apica.usermangement.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class RegisterRequest {
    private String username;
    private String password;
    private Set<Role> roles;
    private String email;
}
