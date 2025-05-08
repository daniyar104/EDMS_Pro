package com.example.docplatform.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String role;

    @Column
    private String firstName;

    @Column(name = "last_name")
    private String last_Name;

    @Column
    private String middleName;

    @Column
    private String iin;

    @Column(name = "birth_year")
    private String birth_year;

    @Column
    private String position;

    @Column(name = "individual_company_number")
    private String individual_company_number;
}
