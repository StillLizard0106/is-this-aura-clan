import { useState } from 'react';
import { Loader2, PlusCircle, Store, ArrowLeft } from 'lucide-react';
import { Link } from 'react-router-dom';
import { toast } from 'react-toastify';

import { createStaffStall } from '../../api/endpoints';
import { buildStallPayload, validateStallForm } from '../../utils/stallForm';

const initialFormState = {
  stallName: '',
  vendorName: '',
  operatingHours: '',
};

const StaffStallManagementPage = () => {
  const [form, setForm] = useState(initialFormState);
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');

  const updateField = (field) => (event) => {
    const value = event.target.value;
    setForm((current) => ({ ...current, [field]: value }));
    setErrors((current) => ({ ...current, [field]: '' }));
    setSuccessMessage('');
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSuccessMessage('');

    const validationErrors = validateStallForm(form);
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      toast.error('Please complete all stall details.');
      return;
    }

    try {
      setSubmitting(true);
      setErrors({});

      const payload = buildStallPayload(form);
      const response = await createStaffStall(payload);
      const createdStallName = response.data?.stallName || payload.stallName;

      setSuccessMessage(`Created stall: ${createdStallName}`);
      toast.success(`Stall created: ${createdStallName}`);
      setForm(initialFormState);
    } catch (error) {
      const message = error.response?.data?.message || 'Failed to create stall';
      toast.error(message);
      setErrors({ form: message });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="container-custom py-8">
      <div className="mb-6">
        <Link
          to="/staff/dashboard"
          className="inline-flex items-center gap-2 text-sm text-blue-600 hover:text-blue-700"
        >
          <ArrowLeft size={16} />
          Back to Admin Dashboard
        </Link>
        <h1 className="mt-3 text-4xl font-bold text-gray-900">Create Stall</h1>
        <p className="text-gray-600 mt-2">
          Add a new canteen stall and make it available for staff management.
        </p>
      </div>

      <div className="grid lg:grid-cols-[1.3fr_0.9fr] gap-6">
        <div className="card">
          <div className="flex items-center gap-3 mb-6">
            <div className="w-12 h-12 rounded-xl bg-blue-100 text-blue-700 flex items-center justify-center">
              <PlusCircle size={24} />
            </div>
            <div>
              <h2 className="text-2xl font-semibold text-gray-900">Stall Details</h2>
              <p className="text-sm text-gray-600">All fields are required.</p>
            </div>
          </div>

          {errors.form && (
            <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
              {errors.form}
            </div>
          )}

          {successMessage && (
            <div className="mb-4 rounded-lg border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700">
              {successMessage}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="form-group">
              <label htmlFor="stallName" className="form-label">
                Stall Name
              </label>
              <input
                id="stallName"
                type="text"
                className="input-field"
                placeholder="e.g. Pizza Stall"
                value={form.stallName}
                onChange={updateField('stallName')}
                disabled={submitting}
              />
              {errors.stallName && <p className="mt-2 text-sm text-red-600">{errors.stallName}</p>}
            </div>

            <div className="form-group">
              <label htmlFor="vendorName" className="form-label">
                Vendor Name
              </label>
              <input
                id="vendorName"
                type="text"
                className="input-field"
                placeholder="e.g. Tony's Pizzeria"
                value={form.vendorName}
                onChange={updateField('vendorName')}
                disabled={submitting}
              />
              {errors.vendorName && <p className="mt-2 text-sm text-red-600">{errors.vendorName}</p>}
            </div>

            <div className="form-group">
              <label htmlFor="operatingHours" className="form-label">
                Operating Hours
              </label>
              <input
                id="operatingHours"
                type="text"
                className="input-field"
                placeholder="e.g. 10:00 AM - 2:00 PM"
                value={form.operatingHours}
                onChange={updateField('operatingHours')}
                disabled={submitting}
              />
              {errors.operatingHours && (
                <p className="mt-2 text-sm text-red-600">{errors.operatingHours}</p>
              )}
            </div>

            <button
              type="submit"
              className="btn-primary w-full flex items-center justify-center gap-2"
              disabled={submitting}
            >
              {submitting ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <Store className="h-4 w-4" />
              )}
              {submitting ? 'Creating Stall...' : 'Create Stall'}
            </button>
          </form>
        </div>

        <div className="space-y-6">
          <div className="card bg-gradient-to-br from-blue-50 to-indigo-100 border border-blue-100">
            <h3 className="text-xl font-semibold text-gray-900 mb-3">What gets saved</h3>
            <ul className="space-y-2 text-sm text-gray-700">
              <li>- Stall name must be unique.</li>
              <li>- Vendor name and operating hours are required.</li>
              <li>- Queue limit defaults to 100.</li>
            </ul>
          </div>

          <div className="card">
            <h3 className="text-xl font-semibold text-gray-900 mb-3">Next Steps</h3>
            <div className="space-y-2">
              <Link to="/staff/admin" className="block btn-secondary text-center">
                Manage All Stalls
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StaffStallManagementPage;
