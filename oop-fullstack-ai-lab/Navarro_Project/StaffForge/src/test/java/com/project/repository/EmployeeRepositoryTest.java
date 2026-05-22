package com.project.repository;

import com.project.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee(
                null,
                "John",
                "Doe",
                "john.doe@example.com",
                "Engineering",
                "Software Engineer"
        );
    }

    @Test
    void testSaveEmployee() {
        // Act
        Employee savedEmployee = employeeRepository.save(testEmployee);

        // Assert
        assertNotNull(savedEmployee.getId());
        assertEquals("John", savedEmployee.getFirstName());
        assertEquals("john.doe@example.com", savedEmployee.getEmail());
    }

    @Test
    void testFindEmployeeById() {
        // Arrange
        Employee savedEmployee = employeeRepository.save(testEmployee);

        // Act
        Optional<Employee> foundEmployee = employeeRepository.findById(savedEmployee.getId());

        // Assert
        assertTrue(foundEmployee.isPresent());
        assertEquals("John", foundEmployee.get().getFirstName());
    }

    @Test
    void testFindEmployeeById_NotFound() {
        // Act
        Optional<Employee> foundEmployee = employeeRepository.findById(999L);

        // Assert
        assertFalse(foundEmployee.isPresent());
    }

    @Test
    void testUpdateEmployee() {
        // Arrange
        Employee savedEmployee = employeeRepository.save(testEmployee);
        savedEmployee.setFirstName("Jane");
        savedEmployee.setDepartment("Marketing");

        // Act
        Employee updatedEmployee = employeeRepository.save(savedEmployee);

        // Assert
        assertEquals("Jane", updatedEmployee.getFirstName());
        assertEquals("Marketing", updatedEmployee.getDepartment());
    }

    @Test
    void testDeleteEmployee() {
        // Arrange
        Employee savedEmployee = employeeRepository.save(testEmployee);
        Long employeeId = savedEmployee.getId();

        // Act
        employeeRepository.delete(savedEmployee);

        // Assert
        Optional<Employee> deletedEmployee = employeeRepository.findById(employeeId);
        assertFalse(deletedEmployee.isPresent());
    }

    @Test
    void testFindAllEmployees() {
        // Arrange
        employeeRepository.save(testEmployee);
        Employee anotherEmployee = new Employee(
                null,
                "Jane",
                "Smith",
                "jane.smith@example.com",
                "Marketing",
                "Marketing Manager"
        );
        employeeRepository.save(anotherEmployee);

        // Act
        var allEmployees = employeeRepository.findAll();

        // Assert
        assertTrue(allEmployees.size() >= 2);
    }
}
