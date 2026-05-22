import { describe, it, expect, beforeEach, vi } from 'vitest';
import axios from 'axios';
import {
  getAllEmployees,
  getEmployeeById,
  createEmployee,
  updateEmployee,
  deleteEmployee,
} from './employeeService';

vi.mock('axios');
const mockedAxios = axios as unknown as { create: ReturnType<typeof vi.fn> };

describe('employeeService', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('getAllEmployees', () => {
    it('should fetch all employees', async () => {
      const mockEmployees = [
        {
          id: 1,
          firstName: 'John',
          lastName: 'Doe',
          email: 'john@example.com',
          department: 'Engineering',
          position: 'Software Engineer',
        },
      ];

      const mockGet = vi.fn().mockResolvedValue({ data: mockEmployees });
      vi.mocked(mockedAxios.create).mockReturnValue({ get: mockGet } as any);

      const result = await getAllEmployees();

      expect(result).toEqual(mockEmployees);
    });

    it('should handle errors', async () => {
      const mockGet = vi.fn().mockRejectedValue(new Error('Network error'));
      vi.mocked(mockedAxios.create).mockReturnValue({ get: mockGet } as any);

      await expect(getAllEmployees()).rejects.toThrow();
    });
  });

  describe('getEmployeeById', () => {
    it('should fetch employee by ID', async () => {
      const mockEmployee = {
        id: 1,
        firstName: 'John',
        lastName: 'Doe',
        email: 'john@example.com',
        department: 'Engineering',
        position: 'Software Engineer',
      };

      const mockGet = vi.fn().mockResolvedValue({ data: mockEmployee });
      vi.mocked(mockedAxios.create).mockReturnValue({ get: mockGet } as any);

      const result = await getEmployeeById(1);

      expect(result).toEqual(mockEmployee);
    });
  });

  describe('createEmployee', () => {
    it('should create a new employee', async () => {
      const newEmployee = {
        firstName: 'Jane',
        lastName: 'Smith',
        email: 'jane@example.com',
        department: 'Marketing',
        position: 'Marketing Manager',
      };

      const createdEmployee = { id: 1, ...newEmployee };

      const mockPost = vi.fn().mockResolvedValue({ data: createdEmployee });
      vi.mocked(mockedAxios.create).mockReturnValue({ post: mockPost } as any);

      const result = await createEmployee(newEmployee);

      expect(result).toEqual(createdEmployee);
    });
  });

  describe('updateEmployee', () => {
    it('should update an employee', async () => {
      const updatedEmployee = {
        id: 1,
        firstName: 'Jane',
        lastName: 'Smith',
        email: 'jane@example.com',
        department: 'Marketing',
        position: 'Marketing Manager',
      };

      const mockPut = vi.fn().mockResolvedValue({ data: updatedEmployee });
      vi.mocked(mockedAxios.create).mockReturnValue({ put: mockPut } as any);

      const result = await updateEmployee(1, updatedEmployee);

      expect(result).toEqual(updatedEmployee);
    });
  });

  describe('deleteEmployee', () => {
    it('should delete an employee', async () => {
      const mockDelete = vi.fn().mockResolvedValue({ data: {} });
      vi.mocked(mockedAxios.create).mockReturnValue({ delete: mockDelete } as any);

      await expect(deleteEmployee(1)).resolves.toBeUndefined();
    });
  });
});
