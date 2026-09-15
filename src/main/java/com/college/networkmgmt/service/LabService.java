package com.college.networkmgmt.service;

import com.college.networkmgmt.model.Lab;
import com.college.networkmgmt.repository.LabRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LabService {

    private final LabRepository labRepository;

    public LabService(LabRepository labRepository) {
        this.labRepository = labRepository;
    }

    @Transactional(readOnly = true)
    public List<Lab> findAll() {
        return labRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Lab> findById(Long id) {
        return labRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Lab> findByDepartmentId(Long departmentId) {
        return labRepository.findByDepartmentId(departmentId);
    }

    public Lab save(Lab lab) {
        if (lab.getLabNumber() != null) {
            lab.setLabNumber(lab.getLabNumber().trim().toUpperCase());
        }

        if (lab.getDepartment() == null || lab.getDepartment().getId() == null) {
            throw new IllegalArgumentException("A valid department must be selected for this lab.");
        }

        Long deptId = lab.getDepartment().getId();

        // Check unique lab number within department
        if (lab.getId() == null) {
            if (labRepository.existsByLabNumberAndDepartmentId(lab.getLabNumber(), deptId)) {
                throw new IllegalArgumentException("Lab identifier '" + lab.getLabNumber() + "' already exists in this department.");
            }
        } else {
            if (labRepository.existsByLabNumberAndDepartmentIdAndIdNot(lab.getLabNumber(), deptId, lab.getId())) {
                throw new IllegalArgumentException("Lab identifier '" + lab.getLabNumber() + "' is already in use by another lab in this department.");
            }
        }

        return labRepository.save(lab);
    }

    public void deleteById(Long id) {
        labRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long count() {
        return labRepository.count();
    }
}
