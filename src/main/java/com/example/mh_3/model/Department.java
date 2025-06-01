package com.example.mh_3.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "departments")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @OneToMany(mappedBy = "department")
    private List<Doctor> doctors;

    @Column
    private String location;

    @Column
    private String contactNumber;

    @Column
    private String workingHours;
} 