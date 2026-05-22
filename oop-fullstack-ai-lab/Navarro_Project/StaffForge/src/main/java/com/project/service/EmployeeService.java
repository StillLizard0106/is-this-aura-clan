package com.project.service;

import com.project.dto.EmployeeDTO;
import java.util.List;

/**
 * EmployeeService Interface
 * 
 * Vibe: "I define the contract."
 * Enforces Abstraction — the controller depends on this contract, not the implementation.
 * Demonstrates Separation of Concerns — business logic will be in the implementation.
 */
public interface EmployeeService {

    /**
     * Retrieve all employees
     * @return List of EmployeeDTO objects
     */
    List<EmployeeDTO> getAllEmployees();

    /**
     * Retrieve an employee by ID
     * @param id Employee ID
     * @return EmployeeDTO object
     */
    EmployeeDTO getEmployeeById(Long id);

    /**
     * Create a new employee
     * @param dto EmployeeDTO object with employee details
     * @return Created EmployeeDTO object with generated ID
     */
    EmployeeDTO createEmployee(EmployeeDTO dto);

    /**
     * Update an existing employee
     * @param id Employee ID to update
     * @param dto EmployeeDTO object with updated details
     * @return Updated EmployeeDTO object
     */
    EmployeeDTO updateEmployee(Long id, EmployeeDTO dto);

    /**
     * Delete an employee
     * @param id Employee ID to delete
     */
    void deleteEmployee(Long id);
}
