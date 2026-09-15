package com.college.networkmgmt.service;

import com.college.networkmgmt.model.College;
import com.college.networkmgmt.repository.CollegeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CollegeService {

    private final CollegeRepository collegeRepository;

    public CollegeService(CollegeRepository collegeRepository) {
        this.collegeRepository = collegeRepository;
    }

    @Transactional(readOnly = true)
    public List<College> findAll() {
        return collegeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<College> findById(Long id) {
        return collegeRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<College> findByCode(String code) {
        return collegeRepository.findByCode(code);
    }

    public College save(College college) {
        if (college.getCode() != null) {
            college.setCode(college.getCode().trim().toUpperCase());
        }

        // Check unique code
        if (college.getId() == null) {
            if (collegeRepository.existsByCode(college.getCode())) {
                throw new IllegalArgumentException("College code '" + college.getCode() + "' already exists.");
            }
        } else {
            if (collegeRepository.existsByCodeAndIdNot(college.getCode(), college.getId())) {
                throw new IllegalArgumentException("College code '" + college.getCode() + "' is used by another college.");
            }
        }

        return collegeRepository.save(college);
    }

    public void deleteById(Long id) {
        collegeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return collegeRepository.count();
    }
}
