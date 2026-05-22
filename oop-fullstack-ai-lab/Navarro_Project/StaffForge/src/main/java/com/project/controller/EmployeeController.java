package com.project.controller;

import com.project.dto.EmployeeDTO;
import com.project.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * EmployeeController
 * 
 * Vibe: "I am just a translator. I receive HTTP, delegate to service, return HTTP."
 * No business logic here. No database access. Just input/output.
 * Demonstrates Separation of Concerns — controller orchestrates, service thinks.
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * GET /api/employees
     * Retrieve all employees
     * @return 200 OK with list of employees
     */
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    /**
     * GET /api/employees/{id}
     * Retrieve a specific employee by ID
     * @param id Employee ID
     * @return 200 OK with employee data
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        EmployeeDTO employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    /**
     * POST /api/employees
     * Create a new employee
     * @param dto Employee data from request body
     * @return 201 Created with created employee data
     */
    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@RequestBody EmployeeDTO dto) {
        EmployeeDTO createdEmployee = employeeService.createEmployee(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
    }

    /**
     * PUT /api/employees/{id}
     * Update an existing employee
     * @param id Employee ID
     * @param dto Updated employee data
     * @return 200 OK with updated employee data
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable Long id,
            @RequestBody EmployeeDTO dto) {
        EmployeeDTO updatedEmployee = employeeService.updateEmployee(id, dto);
        return ResponseEntity.ok(updatedEmployee);
    }

    /**
     * DELETE /api/employees/{id}
     * Delete an employee
     * @param id Employee ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
