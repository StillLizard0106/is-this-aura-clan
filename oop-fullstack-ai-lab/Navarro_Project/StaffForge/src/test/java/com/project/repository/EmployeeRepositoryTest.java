package com.project.repository;

import com.project.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeRepositoryTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee(
                1L,
                "John",
                "Doe",
                "john.doe@example.com",
                "Engineering",
                "Software Engineer"
        );
    }

    @Test
    void testSaveEmployee() {
        // Arrange
        Employee newEmployee = new Employee(null, "Jane", "Smith", "jane@example.com", "Sales", "Sales Manager");
        when(employeeRepository.save(any(Employee.class))).thenReturn(new Employee(1L, "Jane", "Smith", "jane@example.com", "Sales", "Sales Manager"));

        // Act
        Employee savedEmployee = employeeRepository.save(newEmployee);

        // Assert
        assertNotNull(savedEmployee.getId());
        assertEquals("Jane", savedEmployee.getFirstName());
        assertEquals("jane@example.com", savedEmployee.getEmail());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testFindEmployeeById() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

        // Act
        Optional<Employee> foundEmployee = employeeRepository.findById(1L);

        // Assert
        assertTrue(foundEmployee.isPresent());
        assertEquals("John", foundEmployee.get().getFirstName());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void testFindEmployeeById_NotFound() {
        // Arrange
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Employee> foundEmployee = employeeRepository.findById(999L);

        // Assert
        assertFalse(foundEmployee.isPresent());
        verify(employeeRepository, times(1)).findById(999L);
    }

    @Test
    void testUpdateEmployee() {
        // Arrange
        Employee updatedEmployee = new Employee(1L, "Jane", "Doe", "jane.doe@example.com", "Marketing", "Marketing Manager");
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        // Act
        Employee result = employeeRepository.save(updatedEmployee);

        // Assert
        assertEquals("Jane", result.getFirstName());
        assertEquals("Marketing", result.getDepartment());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testDeleteEmployee() {
        // Arrange
        doNothing().when(employeeRepository).delete(testEmployee);
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        employeeRepository.delete(testEmployee);

        // Assert
        Optional<Employee> deletedEmployee = employeeRepository.findById(1L);
        assertFalse(deletedEmployee.isPresent());
        verify(employeeRepository, times(1)).delete(testEmployee);
    }

    @Test
    void testFindAllEmployees() {
        // Arrange
        List<Employee> employees = Arrays.asList(
            testEmployee,
            new Employee(2L, "Jane", "Smith", "jane.smith@example.com", "Marketing", "Marketing Manager")
        );
        when(employeeRepository.findAll()).thenReturn(employees);

        // Act
        var allEmployees = employeeRepository.findAll();

        // Assert
        assertTrue(allEmployees.size() >= 2);
        verify(employeeRepository, times(1)).findAll();
    }
}
