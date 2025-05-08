package com.example.docplatform.dto;

import lombok.Data;

@Data
public class SingUpRequest {
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
}
