package com.project.service;

import com.project.dto.EmployeeDTO;
import com.project.entity.Employee;
import com.project.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * EmployeeServiceImpl
 * 
 * Vibe: "I do the thinking. I apply business logic."
 * Maps between DTO and Entity. Orchestrates between Controller and Repository.
 * Demonstrates Separation of Concerns — business logic lives here, not in the controller.
 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    /**
     * Retrieve all employees and map to DTO list
     */
    @Override
    public List<EmployeeDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Retrieve an employee by ID and map to DTO
     */
    @Override
    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        return mapToDTO(employee);
    }

    /**
     * Create a new employee from DTO
     */
    @Override
    public EmployeeDTO createEmployee(EmployeeDTO dto) {
        Employee employee = mapToEntity(dto);
        Employee savedEmployee = employeeRepository.save(employee);
        return mapToDTO(savedEmployee);
    }

    /**
     * Update an existing employee
     */
    @Override
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO dto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setDepartment(dto.getDepartment());
        employee.setPosition(dto.getPosition());

        Employee updatedEmployee = employeeRepository.save(employee);
        return mapToDTO(updatedEmployee);
    }

    /**
     * Delete an employee
     */
    @Override
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        employeeRepository.delete(employee);
    }

    /**
     * Helper method: Map Entity to DTO
     */
    private EmployeeDTO mapToDTO(Employee employee) {
        return new EmployeeDTO(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getDepartment(),
                employee.getPosition()
        );
    }

    /**
     * Helper method: Map DTO to Entity
     */
    private Employee mapToEntity(EmployeeDTO dto) {
        return new Employee(
                null, // ID is auto-generated
                dto.getFirstName(),
                dto.getLastName(),
                dto.getEmail(),
                dto.getDepartment(),
                dto.getPosition()
        );
    }
}
