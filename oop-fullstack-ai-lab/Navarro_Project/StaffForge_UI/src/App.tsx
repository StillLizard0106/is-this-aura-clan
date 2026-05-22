import React, { useState } from 'react';
import { Employee } from './types/Employee';
import { useEmployees } from './hooks/useEmployees';
import { EmployeeForm } from './components/EmployeeForm';
import { EmployeeList } from './components/EmployeeList';
import './App.css';

/**
 * App Component
 *
 * Vibe: "I orchestrate. I wire everything together."
 * Demonstrates Separation of Concerns — app manages state and callbacks.
 * Demonstrates Composition — assembles components into a working application.
 *
 * Responsibilities:
 *   - Manage edit/add mode
 *   - Wire up hook callbacks to component handlers
 *   - Coordinate data flow between Form and List
 */

export const App: React.FC = () => {
  const {
    employees,
    loading,
    error,
    fetchAll,
    addEmployee,
    editEmployee,
    removeEmployee,
  } = useEmployees();

  const [selectedEmployee, setSelectedEmployee] = useState<Employee | undefined>();
  const [showForm, setShowForm] = useState(false);

  /**
   * Handle form submission (create or update)
   */
  const handleSubmitForm = async (data: Employee): Promise<void> => {
    try {
      if (selectedEmployee?.id) {
        // Update mode
        await editEmployee(selectedEmployee.id, data);
      } else {
        // Create mode
        await addEmployee(data);
      }
      // Clear selection and hide form after successful submission
      setSelectedEmployee(undefined);
      setShowForm(false);
    } catch (err) {
      console.error('Form submission failed:', err);
    }
  };

  /**
   * Handle edit button click
   */
  const handleEdit = (employee: Employee): void => {
    setSelectedEmployee(employee);
    setShowForm(true);
  };

  /**
   * Handle delete button click
   */
  const handleDelete = async (id: number): Promise<void> => {
    if (window.confirm('Are you sure you want to delete this employee?')) {
      try {
        await removeEmployee(id);
      } catch (err) {
        console.error('Delete failed:', err);
      }
    }
  };

  /**
   * Handle cancel form
   */
  const handleCancelForm = (): void => {
    setSelectedEmployee(undefined);
    setShowForm(false);
  };

  /**
   * Handle add new employee
   */
  const handleAddNew = (): void => {
    setSelectedEmployee(undefined);
    setShowForm(true);
  };

  return (
    <div className="app-container">
      <header className="app-header">
        <h1>StaffForge</h1>
        <p>Employee Management System</p>
      </header>

      <main className="app-main">
        {error && <div className="error-banner">{error}</div>}

        <div className="app-grid">
          {/* Form Section */}
          <section className="form-section">
            {showForm ? (
              <EmployeeForm
                onSubmit={handleSubmitForm}
                initialData={selectedEmployee}
                onCancel={handleCancelForm}
              />
            ) : (
              <div className="add-employee-prompt">
                <button
                  onClick={handleAddNew}
                  className="add-new-btn"
                  disabled={loading}
                >
                  + Add New Employee
                </button>
              </div>
            )}
          </section>

          {/* List Section */}
          <section className="list-section">
            <EmployeeList
              employees={employees}
              onEdit={handleEdit}
              onDelete={handleDelete}
              loading={loading}
            />
          </section>
        </div>
      </main>

      <footer className="app-footer">
        <p>&copy; 2026 StaffForge. All rights reserved.</p>
      </footer>
    </div>
  );
};

export default App;
