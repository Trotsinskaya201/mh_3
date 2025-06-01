package com.example.mh_3.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "doctors")
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String specialization;

    @Column(nullable = false)
    private String licenseNumber;

    @Column(nullable = false)
    private String education;

    @Column
    private String experience;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Column
    private String workingHours;

    @Column
    private String qualifications;

    @Column
    private String certifications;
} 