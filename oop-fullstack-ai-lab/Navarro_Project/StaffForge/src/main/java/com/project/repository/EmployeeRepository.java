package com.project.repository;

import com.project.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * EmployeeRepository
 * 
 * Vibe: "I only talk to the database."
 * Extends JpaRepository to inherit CRUD operations.
 * Demonstrates Abstraction and Polymorphism — Spring provides the implementation at runtime.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // CRUD operations inherited from JpaRepository
}
