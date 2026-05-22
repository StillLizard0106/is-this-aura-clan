import React from 'react';
import { Employee } from '../types/Employee';
import './EmployeeList.css';

/**
 * EmployeeList Component
 *
 * Vibe: "I just render. I don't think."
 * Demonstrates Encapsulation — receives all data via props.
 * Demonstrates Separation of Concerns — pure presentational component.
 *
 * Props:
 *   - employees: Array of employee objects to display
 *   - onEdit: Callback when Edit button is clicked
 *   - onDelete: Callback when Delete button is clicked
 *   - loading: Boolean indicating loading state
 */

interface EmployeeListProps {
  employees: Employee[];
  onEdit: (employee: Employee) => void;
  onDelete: (id: number) => void;
  loading: boolean;
}

export const EmployeeList: React.FC<EmployeeListProps> = ({
  employees,
  onEdit,
  onDelete,
  loading,
}) => {
  if (loading && employees.length === 0) {
    return <div className="loading">Loading employees...</div>;
  }

  if (employees.length === 0) {
    return (
      <div className="empty-state">
        <p>No employees found. Create one to get started!</p>
      </div>
    );
  }

  return (
    <div className="employee-list">
      <h2>Employee Directory</h2>
      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>First Name</th>
              <th>Last Name</th>
              <th>Email</th>
              <th>Department</th>
              <th>Position</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {employees.map((employee) => (
              <tr key={employee.id}>
                <td>{employee.id}</td>
                <td>{employee.firstName}</td>
                <td>{employee.lastName}</td>
                <td>{employee.email}</td>
                <td>{employee.department}</td>
                <td>{employee.position}</td>
                <td className="actions">
                  <button
                    onClick={() => onEdit(employee)}
                    className="edit-btn"
                    disabled={loading}
                    title="Edit employee"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => employee.id && onDelete(employee.id)}
                    className="delete-btn"
                    disabled={loading}
                    title="Delete employee"
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
