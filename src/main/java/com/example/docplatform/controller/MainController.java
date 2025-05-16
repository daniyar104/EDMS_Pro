package com.example.docplatform.controller;

import com.example.docplatform.dto.company.UserWithCompanyDTO;
import com.example.docplatform.model.User;
import com.example.docplatform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/secured")
@RequiredArgsConstructor
public class MainController {

    private final UserService userService;

    @GetMapping("/user")
    public String userAccess(Principal principal){
        if (principal == null){
            return "Null";
        }else {
            return "Hello, " + principal.getName();
        }
    }


    @GetMapping("/profile")
    public ResponseEntity<?> profileAccess(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No user authenticated");
        }

        String email = principal.getName(); // Получаем email из контекста безопасности

        // Получаем информацию о пользователе и его компании
        try {
            UserWithCompanyDTO userWithCompanyDTO = userService.getUserWithCompanyInfo(email);
            return ResponseEntity.ok(userWithCompanyDTO); // Возвращаем информацию о пользователе и компании
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or company not found");
        }
    }
    @GetMapping("/by-company/{companyCode}")
    public ResponseEntity<List<User>> getUsersByCompanyCode(@PathVariable String companyCode) {
        List<User> users = userService.getUsersByCompanyCode(companyCode);
        return ResponseEntity.ok(users);
    }


}
