package com.example.mh_3.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "medical_records")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    private LocalDateTime recordDateTime;

    @Column(nullable = false)
    private String diagnosis;

    @Column
    private String symptoms;

    @Column
    private String treatment;

    @Column
    private String medications;

    @Column
    private String testResults;

    @Column
    private String recommendations;

    @Column
    private String followUpInstructions;

    @Column
    private String notes;
} 