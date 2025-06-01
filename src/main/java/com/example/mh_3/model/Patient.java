package com.example.mh_3.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String passportNumber;

    @Column(nullable = false)
    private String insuranceNumber;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phoneNumber;

    @Column
    private String bloodType;

    @Column
    private String allergies;

    @Column
    private String chronicDiseases;

    @Column
    private String emergencyContact;

    @Column
    private String emergencyPhone;
} 