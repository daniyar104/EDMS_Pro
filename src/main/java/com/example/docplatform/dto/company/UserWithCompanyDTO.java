package com.example.docplatform.dto.company;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWithCompanyDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String position;
    private String companyName;
    private String companyAddress;
    private String companyCode;
}