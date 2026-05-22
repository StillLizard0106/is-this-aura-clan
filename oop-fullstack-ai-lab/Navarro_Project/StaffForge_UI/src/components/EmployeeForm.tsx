import React, { useState, useEffect } from 'react';
import { Employee } from '../types/Employee';
import './EmployeeForm.css';

/**
 * EmployeeForm Component
 *
 * Vibe: "I handle form input and submission."
 * Demonstrates Encapsulation — form state lives inside this component.
 * Props:
 *   - onSubmit: Callback when form is submitted
 *   - initialData: Optional employee data for edit mode
 *   - onCancel: Callback to cancel form (go back to list)
 */

interface EmployeeFormProps {
  onSubmit: (data: Employee) => Promise<void>;
  initialData?: Employee;
  onCancel: () => void;
}

export const EmployeeForm: React.FC<EmployeeFormProps> = ({
  onSubmit,
  initialData,
  onCancel,
}) => {
  const [formData, setFormData] = useState<Employee>(
    initialData || {
      firstName: '',
      lastName: '',
      email: '',
      department: '',
      position: '',
    }
  );

  const [errors, setErrors] = useState<Partial<Record<keyof Employee, string>>>({});
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (initialData) {
      setFormData(initialData);
    }
  }, [initialData]);

  /**
   * Validate form inputs
   */
  const validateForm = (): boolean => {
    const newErrors: typeof errors = {};

    if (!formData.firstName.trim()) {
      newErrors.firstName = 'First name is required';
    }
    if (!formData.lastName.trim()) {
      newErrors.lastName = 'Last name is required';
    }
    if (!formData.email.trim()) {
      newErrors.email = 'Email is required';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = 'Invalid email format';
    }
    if (!formData.department.trim()) {
      newErrors.department = 'Department is required';
    }
    if (!formData.position.trim()) {
      newErrors.position = 'Position is required';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  /**
   * Handle input change
   */
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    // Clear error for this field when user starts typing
    if (errors[name as keyof Employee]) {
      setErrors((prev) => ({
        ...prev,
        [name]: undefined,
      }));
    }
  };

  /**
   * Handle form submission
   */
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setLoading(true);
    try {
      await onSubmit(formData);
      // Reset form after successful submission
      setFormData({
        firstName: '',
        lastName: '',
        email: '',
        department: '',
        position: '',
      });
    } catch (error) {
      console.error('Form submission error:', error);
    } finally {
      setLoading(false);
    }
  };

  const isEditMode = !!initialData?.id;
  const submitButtonLabel = isEditMode ? 'Update Employee' : 'Add Employee';

  return (
    <form onSubmit={handleSubmit} className="employee-form">
      <h2>{isEditMode ? 'Edit Employee' : 'Add New Employee'}</h2>

      <div className="form-group">
        <label htmlFor="firstName">First Name *</label>
        <input
          type="text"
          id="firstName"
          name="firstName"
          value={formData.firstName}
          onChange={handleInputChange}
          placeholder="Enter first name"
          disabled={loading}
        />
        {errors.firstName && <span className="error">{errors.firstName}</span>}
      </div>

      <div className="form-group">
        <label htmlFor="lastName">Last Name *</label>
        <input
          type="text"
          id="lastName"
          name="lastName"
          value={formData.lastName}
          onChange={handleInputChange}
          placeholder="Enter last name"
          disabled={loading}
        />
        {errors.lastName && <span className="error">{errors.lastName}</span>}
      </div>

      <div className="form-group">
        <label htmlFor="email">Email *</label>
        <input
          type="email"
          id="email"
          name="email"
          value={formData.email}
          onChange={handleInputChange}
          placeholder="Enter email"
          disabled={loading}
        />
        {errors.email && <span className="error">{errors.email}</span>}
      </div>

      <div className="form-group">
        <label htmlFor="department">Department *</label>
        <select
          id="department"
          name="department"
          value={formData.department}
          onChange={handleInputChange}
          disabled={loading}
        >
          <option value="">Select a department</option>
          <option value="Engineering">Engineering</option>
          <option value="Marketing">Marketing</option>
          <option value="Sales">Sales</option>
          <option value="Management">Management</option>
          <option value="HR">HR</option>
        </select>
        {errors.department && <span className="error">{errors.department}</span>}
      </div>

      <div className="form-group">
        <label htmlFor="position">Position *</label>
        <input
          type="text"
          id="position"
          name="position"
          value={formData.position}
          onChange={handleInputChange}
          placeholder="Enter position"
          disabled={loading}
        />
        {errors.position && <span className="error">{errors.position}</span>}
      </div>

      <div className="form-actions">
        <button type="submit" disabled={loading}>
          {loading ? 'Saving...' : submitButtonLabel}
        </button>
        <button
          type="button"
          onClick={onCancel}
          disabled={loading}
          className="cancel-btn"
        >
          Cancel
        </button>
      </div>
    </form>
  );
};
