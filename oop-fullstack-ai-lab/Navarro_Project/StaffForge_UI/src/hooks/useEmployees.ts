import { useState, useEffect, useCallback } from 'react';
import { Employee } from '../types/Employee';
import {
  getAllEmployees,
  getEmployeeById,
  createEmployee,
  updateEmployee,
  deleteEmployee,
} from '../services/employeeService';

/**
 * useEmployees Custom Hook
 *
 * Vibe: "I manage all employee state and operations."
 * Demonstrates Encapsulation — state logic is hidden from UI components.
 * Demonstrates Separation of Concerns — business logic is separated from rendering.
 */

interface UseEmployeesReturn {
  employees: Employee[];
  loading: boolean;
  error: string | null;
  fetchAll: () => void;
  addEmployee: (data: Employee) => Promise<void>;
  editEmployee: (id: number, data: Employee) => Promise<void>;
  removeEmployee: (id: number) => Promise<void>;
}

export const useEmployees = (): UseEmployeesReturn => {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  /**
   * Fetch all employees from backend
   */
  const fetchAll = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getAllEmployees();
      setEmployees(data);
    } catch (err) {
      setError('Failed to fetch employees');
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, []);

  /**
   * Add a new employee
   */
  const addEmployee = useCallback(async (data: Employee) => {
    setError(null);
    try {
      const newEmployee = await createEmployee(data);
      setEmployees((prev) => [...prev, newEmployee]);
    } catch (err) {
      setError('Failed to create employee');
      console.error(err);
      throw err;
    }
  }, []);

  /**
   * Edit an existing employee
   */
  const editEmployee = useCallback(async (id: number, data: Employee) => {
    setError(null);
    try {
      const updatedEmployee = await updateEmployee(id, data);
      setEmployees((prev) =>
        prev.map((emp) => (emp.id === id ? updatedEmployee : emp))
      );
    } catch (err) {
      setError('Failed to update employee');
      console.error(err);
      throw err;
    }
  }, []);

  /**
   * Remove an employee
   */
  const removeEmployee = useCallback(async (id: number) => {
    setError(null);
    try {
      await deleteEmployee(id);
      setEmployees((prev) => prev.filter((emp) => emp.id !== id));
    } catch (err) {
      setError('Failed to delete employee');
      console.error(err);
      throw err;
    }
  }, []);

  /**
   * Fetch employees on mount
   */
  useEffect(() => {
    fetchAll();
  }, [fetchAll]);

  return {
    employees,
    loading,
    error,
    fetchAll,
    addEmployee,
    editEmployee,
    removeEmployee,
  };
};
