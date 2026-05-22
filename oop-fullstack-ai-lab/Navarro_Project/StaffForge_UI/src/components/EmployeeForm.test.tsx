import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { EmployeeForm } from './EmployeeForm';

describe('EmployeeForm Component', () => {
  const mockOnSubmit = vi.fn();
  const mockOnCancel = vi.fn();

  beforeEach(() => {
    mockOnSubmit.mockClear();
    mockOnCancel.mockClear();
  });

  it('should render the form with all fields', () => {
    render(
      <EmployeeForm
        onSubmit={mockOnSubmit}
        onCancel={mockOnCancel}
      />
    );

    expect(screen.getByLabelText(/first name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/last name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/department/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/position/i)).toBeInTheDocument();
  });

  it('should display "Add New Employee" title for create mode', () => {
    render(
      <EmployeeForm
        onSubmit={mockOnSubmit}
        onCancel={mockOnCancel}
      />
    );

    expect(screen.getByText(/add new employee/i)).toBeInTheDocument();
  });

  it('should display "Edit Employee" title for edit mode', () => {
    const employee = {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
      department: 'Engineering',
      position: 'Software Engineer',
    };

    render(
      <EmployeeForm
        onSubmit={mockOnSubmit}
        initialData={employee}
        onCancel={mockOnCancel}
      />
    );

    expect(screen.getByText(/edit employee/i)).toBeInTheDocument();
  });

  it('should validate required fields', async () => {
    render(
      <EmployeeForm
        onSubmit={mockOnSubmit}
        onCancel={mockOnCancel}
      />
    );

    const submitButton = screen.getByText(/add employee/i);
    fireEvent.click(submitButton);

    expect(await screen.findByText(/first name is required/i)).toBeInTheDocument();
    expect(screen.getByText(/last name is required/i)).toBeInTheDocument();
    expect(screen.getByText(/email is required/i)).toBeInTheDocument();
  });

  it('should validate email format', async () => {
    render(
      <EmployeeForm
        onSubmit={mockOnSubmit}
        onCancel={mockOnCancel}
      />
    );

    const emailInput = screen.getByLabelText(/email/i);
    fireEvent.change(emailInput, { target: { value: 'invalid-email' } });

    const submitButton = screen.getByText(/add employee/i);
    fireEvent.click(submitButton);

    expect(await screen.findByText(/invalid email format/i)).toBeInTheDocument();
  });

  it('should call onCancel when Cancel button is clicked', () => {
    render(
      <EmployeeForm
        onSubmit={mockOnSubmit}
        onCancel={mockOnCancel}
      />
    );

    const cancelButton = screen.getByText(/cancel/i);
    fireEvent.click(cancelButton);

    expect(mockOnCancel).toHaveBeenCalled();
  });

  it('should populate form with initial data in edit mode', () => {
    const employee = {
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      email: 'john@example.com',
      department: 'Engineering',
      position: 'Software Engineer',
    };

    render(
      <EmployeeForm
        onSubmit={mockOnSubmit}
        initialData={employee}
        onCancel={mockOnCancel}
      />
    );

    expect(screen.getByDisplayValue(/john/i)).toBeInTheDocument();
    expect(screen.getByDisplayValue(/doe/i)).toBeInTheDocument();
    expect(screen.getByDisplayValue(/john@example.com/i)).toBeInTheDocument();
  });
});
