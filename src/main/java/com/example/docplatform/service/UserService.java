package com.example.docplatform.service;

import com.example.docplatform.dto.company.UserWithCompanyDTO;
import com.example.docplatform.model.User;
import com.example.docplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User '%s' not found", email)
        ));
        return UserDetailsImpl.build(user);
    }

    public UserWithCompanyDTO getUserWithCompanyInfo(String email) {
        Optional<UserWithCompanyDTO> userWithCompanyDTO = userRepository.findUserWithCompanyInfoByEmail(email);
        return userWithCompanyDTO.orElseThrow(() -> new RuntimeException("User or company not found"));
    }
}
