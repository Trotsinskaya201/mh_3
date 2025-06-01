package com.example.mh_3.repository;

import com.example.mh_3.model.Newborn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewbornRepository extends JpaRepository<Newborn, Long> {
    List<Newborn> findByMotherId(Long motherId);
    long countByGender(String gender);
    List<Newborn> findByGender(String gender);
} 