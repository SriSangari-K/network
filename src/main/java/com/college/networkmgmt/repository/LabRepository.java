package com.college.networkmgmt.repository;

import com.college.networkmgmt.model.Lab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LabRepository extends JpaRepository<Lab, Long> {
    List<Lab> findByDepartmentId(Long departmentId);
    boolean existsByLabNumberAndDepartmentId(String labNumber, Long departmentId);
    boolean existsByLabNumberAndDepartmentIdAndIdNot(String labNumber, Long departmentId, Long id);
}
