package com.example.docplatform.dto;

import lombok.Data;

@Data
public class SignInRequest {
    private String username;
    private String password;
    private String role;
}
