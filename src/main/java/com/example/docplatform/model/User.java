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
    private String lastName;

    @Column
    private String middleName;

    @Column(name = "iin")
    private String iin;

    @Column(name = "birth_year")
    private String birthYear;

    @Column
    private String position;

    @Column(name = "individual_company_number")
    private String individualCompanyNumber; // Внешний ключ, но без связи с объектом Company напрямую

    // Связь с компанией через внешний ключ
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "individual_company_number", referencedColumnName = "companyCode", insertable = false, updatable = false)
    private Company company; // Компания, к которой относится пользователь
}
