package com.example.mh_3.service;

import com.example.mh_3.model.Newborn;

import java.util.List;

public interface NewbornService {
    List<Newborn> findAllNewborns();
    void saveNewborn(Newborn newborn);
    Newborn findNewbornById(Long id);
    void deleteNewbornById(Long id);
    List<Newborn> findByMotherId(Long motherId);
    long countAllNewborns();
    long countByGender(String gender);
    List<Newborn> findByGender(String gender);
} 