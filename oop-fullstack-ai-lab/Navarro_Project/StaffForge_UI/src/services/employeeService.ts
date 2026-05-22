import axios from 'axios';
import { Employee } from '../types/Employee';

/**
 * employeeService
 *
 * Vibe: "I am the bridge to the backend."
 * Uses axios to communicate with Spring Boot backend.
 * Demonstrates Abstraction — components never call axios directly.
 * Demonstrates Separation of Concerns — HTTP logic is isolated here.
 */

const API_BASE_URL = 'http://localhost:8080/api/employees';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Retrieve all employees
 * @returns Promise<Employee[]> Array of employees
 */
export const getAllEmployees = async (): Promise<Employee[]> => {
  try {
    const response = await apiClient.get<Employee[]>('');
    return response.data;
  } catch (error) {
    console.error('Error fetching employees:', error);
    throw error;
  }
};

/**
 * Retrieve a specific employee by ID
 * @param id Employee ID
 * @returns Promise<Employee> Employee data
 */
export const getEmployeeById = async (id: number): Promise<Employee> => {
  try {
    const response = await apiClient.get<Employee>(`/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching employee with id ${id}:`, error);
    throw error;
  }
};

/**
 * Create a new employee
 * @param data Employee data
 * @returns Promise<Employee> Created employee with ID
 */
export const createEmployee = async (data: Employee): Promise<Employee> => {
  try {
    const response = await apiClient.post<Employee>('', data);
    return response.data;
  } catch (error) {
    console.error('Error creating employee:', error);
    throw error;
  }
};

/**
 * Update an existing employee
 * @param id Employee ID
 * @param data Updated employee data
 * @returns Promise<Employee> Updated employee
 */
export const updateEmployee = async (
  id: number,
  data: Employee
): Promise<Employee> => {
  try {
    const response = await apiClient.put<Employee>(`/${id}`, data);
    return response.data;
  } catch (error) {
    console.error(`Error updating employee with id ${id}:`, error);
    throw error;
  }
};

/**
 * Delete an employee
 * @param id Employee ID
 * @returns Promise<void>
 */
export const deleteEmployee = async (id: number): Promise<void> => {
  try {
    await apiClient.delete(`/${id}`);
  } catch (error) {
    console.error(`Error deleting employee with id ${id}:`, error);
    throw error;
  }
};
