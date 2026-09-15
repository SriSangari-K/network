package com.college.networkmgmt.controller.api;

import com.college.networkmgmt.model.Lab;
import com.college.networkmgmt.service.DepartmentService;
import com.college.networkmgmt.service.LabService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/labs")
public class LabRestController {

    private final LabService labService;
    private final DepartmentService departmentService;

    public LabRestController(LabService labService, DepartmentService departmentService) {
        this.labService = labService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public List<Lab> getAllLabs(@RequestParam(required = false) Long departmentId) {
        if (departmentId != null) {
            return labService.findByDepartmentId(departmentId);
        }
        return labService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getLabById(@PathVariable Long id) {
        return labService.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Lab not found with id: " + id)));
    }

    @PostMapping
    public ResponseEntity<?> createLab(@Valid @RequestBody Lab lab) {
        if (lab.getDepartment() == null || lab.getDepartment().getId() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Department id is required"));
        }
        if (departmentService.findById(lab.getDepartment().getId()).isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Department does not exist with specified id"));
        }
        try {
            Lab created = labService.save(lab);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateLab(@PathVariable Long id, @Valid @RequestBody Lab lab) {
        if (labService.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Lab not found with id: " + id));
        }
        lab.setId(id);
        try {
            Lab updated = labService.save(lab);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLab(@PathVariable Long id) {
        if (labService.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Lab not found with id: " + id));
        }
        labService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Lab deleted successfully"));
    }
}
