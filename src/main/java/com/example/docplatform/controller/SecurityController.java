package com.example.docplatform.controller;

import com.example.docplatform.dto.SignInRequest;
import com.example.docplatform.dto.SingUpRequest;
import com.example.docplatform.model.User;
import com.example.docplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class SecurityController {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("/signup")
    ResponseEntity<?> signup(@RequestBody SignInRequest signInRequest){
        if(userRepository.existsByUsername(signInRequest.getUsername())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Choice other username");
        }

        User user = new User();
        user.setUsername(signInRequest.getUsername());
        user.setPassword(signInRequest.getPassword());
        user.setRole(signInRequest.getRole());
        userRepository.save(user);
        return ResponseEntity.ok("Success!");

    }


    @PostMapping("/signin")
    ResponseEntity<?> signin(@RequestBody SingUpRequest singUpRequest){
        Authentication authentication = null;

        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(singUpRequest.getUsername(), singUpRequest.getPassword()));
        }catch (BadCredentialsException e){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return ResponseEntity.ok("Success!");
    }
}
