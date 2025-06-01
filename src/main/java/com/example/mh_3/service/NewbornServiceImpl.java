package com.example.mh_3.service;

import com.example.mh_3.model.Newborn;
import com.example.mh_3.repository.NewbornRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NewbornServiceImpl implements NewbornService {

    @Autowired
    private NewbornRepository newbornRepository;

    @Override
    public List<Newborn> findAllNewborns() {
        return newbornRepository.findAll();
    }

    @Override
    public void saveNewborn(Newborn newborn) {
        newbornRepository.save(newborn);
    }

    @Override
    public Newborn findNewbornById(Long id) {
        Optional<Newborn> result = newbornRepository.findById(id);
        return result.orElse(null);
    }

    @Override
    public void deleteNewbornById(Long id) {
        newbornRepository.deleteById(id);
    }

    @Override
    public List<Newborn> findByMotherId(Long motherId) {
        return newbornRepository.findByMotherId(motherId);
    }

    @Override
    public long countAllNewborns() {
        return newbornRepository.count();
    }

    @Override
    public long countByGender(String gender) {
        return newbornRepository.countByGender(gender);
    }

    @Override
    public List<Newborn> findByGender(String gender) {
        return newbornRepository.findByGender(gender);
    }
} 