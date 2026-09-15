package com.college.networkmgmt.controller.api;

import com.college.networkmgmt.model.College;
import com.college.networkmgmt.service.CollegeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/colleges")
public class CollegeRestController {

    private final CollegeService collegeService;

    public CollegeRestController(CollegeService collegeService) {
        this.collegeService = collegeService;
    }

    @GetMapping
    public List<College> getAllColleges() {
        return collegeService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCollegeById(@PathVariable Long id) {
        return collegeService.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "College not found with id: " + id)));
    }

    @PostMapping
    public ResponseEntity<?> createCollege(@Valid @RequestBody College college) {
        try {
            College created = collegeService.save(college);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCollege(@PathVariable Long id, @Valid @RequestBody College college) {
        if (collegeService.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "College not found with id: " + id));
        }
        college.setId(id);
        try {
            College updated = collegeService.save(college);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCollege(@PathVariable Long id) {
        if (collegeService.findById(id).isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "College not found with id: " + id));
        }
        collegeService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "College deleted successfully"));
    }
}
