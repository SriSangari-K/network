package com.college.networkmgmt.service;

import com.college.networkmgmt.model.Department;
import com.college.networkmgmt.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Transactional(readOnly = true)
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Department> findById(Long id) {
        return departmentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Department> findByCollegeId(Long collegeId) {
        return departmentRepository.findByCollegeId(collegeId);
    }

    public Department save(Department department) {
        if (department.getCode() != null) {
            department.setCode(department.getCode().trim().toUpperCase());
        }

        if (department.getCollege() == null || department.getCollege().getId() == null) {
            throw new IllegalArgumentException("A valid college must be selected for this department.");
        }

        Long collegeId = department.getCollege().getId();

        // Check unique code within college
        if (department.getId() == null) {
            if (departmentRepository.existsByCodeAndCollegeId(department.getCode(), collegeId)) {
                throw new IllegalArgumentException("Department code '" + department.getCode() + "' already exists in this college.");
            }
        } else {
            if (departmentRepository.existsByCodeAndCollegeIdAndIdNot(department.getCode(), collegeId, department.getId())) {
                throw new IllegalArgumentException("Department code '" + department.getCode() + "' is already in use by another department in this college.");
            }
        }

        return departmentRepository.save(department);
    }

    public void deleteById(Long id) {
        departmentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return departmentRepository.count();
    }
}
