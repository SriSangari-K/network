package com.college.networkmgmt.repository;

import com.college.networkmgmt.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByCollegeId(Long collegeId);
    boolean existsByCodeAndCollegeId(String code, Long collegeId);
    boolean existsByCodeAndCollegeIdAndIdNot(String code, Long collegeId, Long id);
}
