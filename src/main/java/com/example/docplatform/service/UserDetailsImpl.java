package com.example.docplatform.service;

import com.example.docplatform.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String email;
    private String password;
    private String role;
    private String firstName;
    private String last_Name;
    private String middleName;
    private String iin;
    private String birth_year;
    private String position;
    private String individual_company_number;

    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getFirstName(),
                user.getLastName(),
                user.getMiddleName(),
                user.getIin(),
                user.getBirthYear(),
                user.getPosition(),
                user.getIndividualCompanyNumber());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Вы можете добавить логику для возврата ролей или прав доступа, если необходимо
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // Теперь возвращаем email вместо username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
