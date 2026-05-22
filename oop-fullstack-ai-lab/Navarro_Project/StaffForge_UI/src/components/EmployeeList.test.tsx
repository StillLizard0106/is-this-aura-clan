import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { EmployeeList } from './EmployeeList';
import { Employee } from '../types/Employee';

describe('EmployeeList Component', () => {
  const mockOnEdit = vi.fn();
  const mockOnDelete = vi.fn();

  const mockEmployees: Employee[] = [
    {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
      department: 'Engineering',
      position: 'Software Engineer',
    },
    {
      id: 2,
      firstName: 'Jane',
      lastName: 'Smith',
      email: 'jane@example.com',
      department: 'Marketing',
      position: 'Marketing Manager',
    },
  ];

  beforeEach(() => {
    mockOnEdit.mockClear();
    mockOnDelete.mockClear();
  });

  it('should render employee list title', () => {
    render(
      <EmployeeList
        employees={mockEmployees}
        onEdit={mockOnEdit}
        onDelete={mockOnDelete}
        loading={false}
      />
    );

    expect(screen.getByText(/employee directory/i)).toBeInTheDocument();
  });

  it('should render all employees in a table', () => {
    render(
      <EmployeeList
        employees={mockEmployees}
        onEdit={mockOnEdit}
        onDelete={mockOnDelete}
        loading={false}
      />
    );

    expect(screen.getByText(/john/i)).toBeInTheDocument();
    expect(screen.getByText(/jane/i)).toBeInTheDocument();
    expect(screen.getByText(/engineering/i)).toBeInTheDocument();
    expect(screen.getByText(/marketing/i)).toBeInTheDocument();
  });

  it('should display correct table headers', () => {
    render(
      <EmployeeList
        employees={mockEmployees}
        onEdit={mockOnEdit}
        onDelete={mockOnDelete}
        loading={false}
      />
    );

    expect(screen.getByText(/id/i)).toBeInTheDocument();
    expect(screen.getByText(/first name/i)).toBeInTheDocument();
    expect(screen.getByText(/last name/i)).toBeInTheDocument();
    expect(screen.getByText(/email/i)).toBeInTheDocument();
    expect(screen.getByText(/department/i)).toBeInTheDocument();
    expect(screen.getByText(/position/i)).toBeInTheDocument();
    expect(screen.getByText(/actions/i)).toBeInTheDocument();
  });

  it('should display loading message when loading and no employees', () => {
    render(
      <EmployeeList
        employees={[]}
        onEdit={mockOnEdit}
        onDelete={mockOnDelete}
        loading={true}
      />
    );

    expect(screen.getByText(/loading employees/i)).toBeInTheDocument();
  });

  it('should display empty state when no employees', () => {
    render(
      <EmployeeList
        employees={[]}
        onEdit={mockOnEdit}
        onDelete={mockOnDelete}
        loading={false}
      />
    );

    expect(screen.getByText(/no employees found/i)).toBeInTheDocument();
  });

  it('should call onEdit when Edit button is clicked', () => {
    render(
      <EmployeeList
        employees={mockEmployees}
        onEdit={mockOnEdit}
        onDelete={mockOnDelete}
        loading={false}
      />
    );

    const editButtons = screen.getAllByText(/edit/i);
    fireEvent.click(editButtons[0]);

    expect(mockOnEdit).toHaveBeenCalledWith(mockEmployees[0]);
  });

  it('should call onDelete when Delete button is clicked', () => {
    render(
      <EmployeeList
        employees={mockEmployees}
        onEdit={mockOnEdit}
        onDelete={mockOnDelete}
        loading={false}
      />
    );

    const deleteButtons = screen.getAllByText(/delete/i);
    fireEvent.click(deleteButtons[0]);

    expect(mockOnDelete).toHaveBeenCalledWith(1);
  });

  it('should disable buttons when loading', () => {
    render(
      <EmployeeList
        employees={mockEmployees}
        onEdit={mockOnEdit}
        onDelete={mockOnDelete}
        loading={true}
      />
    );

    const editButtons = screen.getAllByText(/edit/i);
    const deleteButtons = screen.getAllByText(/delete/i);

    expect(editButtons[0]).toBeDisabled();
    expect(deleteButtons[0]).toBeDisabled();
  });
});
