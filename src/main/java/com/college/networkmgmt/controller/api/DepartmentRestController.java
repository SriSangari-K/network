package com.college.networkmgmt.controller.api;

import com.college.networkmgmt.model.Department;
import com.college.networkmgmt.service.CollegeService;
import com.college.networkmgmt.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/departments")
public class DepartmentRestController {

    private final DepartmentService departmentService;
    private final CollegeService collegeService;

    public DepartmentRestController(DepartmentService departmentService, CollegeService collegeService) {
        this.departmentService = departmentService;
        this.collegeService = collegeService;
    }

    @GetMapping
    public List<Department> getAllDepartments(@RequestParam(required = false) Long collegeId) {
        if (collegeId != null) {
            return departmentService.findByCollegeId(collegeId);
        }
        return departmentService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartmentById(@PathVariable Long id) {
        return departmentService.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Department not found with id: " + id)));
    }

    @PostMapping
    public ResponseEntity<?> createDepartment(@Valid @RequestBody Department department) {
        if (department.getCollege() == null || department.getCollege().getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "College id is required"));
        }
        if (collegeService.findById(department.getCollege().getId()).isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "College does not exist with specified id"));
        }
        try {
            Department created = departmentService.save(department);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id, @Valid @RequestBody Department department) {
        if (departmentService.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Department not found with id: " + id));
        }
        department.setId(id);
        try {
            Department updated = departmentService.save(department);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id) {
        if (departmentService.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Department not found with id: " + id));
        }
        departmentService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Department deleted successfully"));
    }
}
