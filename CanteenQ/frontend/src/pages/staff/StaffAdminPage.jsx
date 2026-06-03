import { useCallback, useEffect, useState } from 'react';
import { AlertCircle, Loader2, PenLine, PlusCircle, RotateCcw, Trash2 } from 'lucide-react';
import { Link } from 'react-router-dom';
import { toast } from 'react-toastify';

import {
  assignStaffToStall,
  createStaffStall,
  deleteStaffStall,
  getAdminStallAssignments,
  getAllStaffStalls,
  removeStaffFromStall,
  updateStaffStall,
} from '../../api/endpoints';
import { buildStallPayload, validateStallForm } from '../../utils/stallForm';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const initialFormState = {
  stallName: '',
  vendorName: '',
  operatingHours: '',
};

const StaffAdminPage = () => {
  const [stalls, setStalls] = useState([]);
  const [selectedStallId, setSelectedStallId] = useState('');
  const [assignments, setAssignments] = useState([]);
  const [assignmentEmail, setAssignmentEmail] = useState('');
  const [assignmentLoading, setAssignmentLoading] = useState(false);
  const [assignmentSuccess, setAssignmentSuccess] = useState('');
  const [assignmentError, setAssignmentError] = useState('');
  const [loadingAssignments, setLoadingAssignments] = useState(false);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [deletingId, setDeletingId] = useState(null);
  const [editingId, setEditingId] = useState(null);
  const [form, setForm] = useState(initialFormState);
  const [errors, setErrors] = useState({});
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const fetchStalls = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      const response = await getAllStaffStalls();
      const loadedStalls = response.data ?? [];
      setStalls(loadedStalls);
      setSelectedStallId((current) => current || String(loadedStalls[0]?.id || ''));
    } catch (fetchError) {
      const message = fetchError.response?.data?.message || 'Failed to load stalls';
      setError(message);
      toast.error(message);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void Promise.resolve().then(fetchStalls);
  }, [fetchStalls]);

  const resetForm = () => {
    setForm(initialFormState);
    setEditingId(null);
    setErrors({});
    setSuccessMessage('');
  };

  const startEdit = (stall) => {
    setEditingId(stall.id);
    setForm({
      stallName: stall.stallName || '',
      vendorName: stall.vendorName || '',
      operatingHours: stall.operatingHours || '',
    });
    setSuccessMessage('');
    setErrors({});
    setError('');
  };

  const updateField = (field) => (event) => {
    const value = event.target.value;
    setForm((current) => ({ ...current, [field]: value }));
    setErrors((current) => ({ ...current, [field]: '' }));
    setSuccessMessage('');
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setSuccessMessage('');

    const validationErrors = validateStallForm(form);
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      toast.error('Please complete all stall details.');
      return;
    }

    try {
      setSubmitting(true);
      const payload = buildStallPayload(form);

      if (editingId) {
        await updateStaffStall(editingId, payload);
        toast.success('Stall updated.');
        setSuccessMessage('Stall updated successfully.');
      } else {
        await createStaffStall(payload);
        toast.success('Stall created.');
        setSuccessMessage('Stall created successfully.');
      }

      await fetchStalls();
      resetForm();
    } catch (submitError) {
      const message = submitError.response?.data?.message || 'Failed to save stall';
      setError(message);
      toast.error(message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (stallId) => {
    if (!window.confirm('Delete this stall? This cannot be undone.')) return;
    try {
      setDeletingId(stallId);
      setError('');
      await deleteStaffStall(stallId);
      toast.success('Stall deleted.');
      // Refresh stalls list and clear selection if necessary
      await fetchStalls();
      if (String(selectedStallId) === String(stallId)) {
        setSelectedStallId('');
        setAssignments([]);
      }
    } catch (deleteError) {
      const message = deleteError.response?.data?.message || 'Failed to delete stall';
      setError(message);
      toast.error(message);
    } finally {
      setDeletingId(null);
    }
  };

  const fetchAssignments = useCallback(async (stallId) => {
    if (!stallId) {
      setAssignments([]);
      return;
    }

    try {
      setLoadingAssignments(true);
      setAssignmentError('');
      const response = await getAdminStallAssignments(stallId);
      setAssignments(response.data ?? []);
    } catch (fetchError) {
      const message = fetchError.response?.data?.message || 'Failed to load assignments';
      setAssignmentError(message);
      toast.error(message);
    } finally {
      setLoadingAssignments(false);
    }
  }, []);

  useEffect(() => {
    if (selectedStallId) {
      void fetchAssignments(selectedStallId);
    }
  }, [selectedStallId, fetchAssignments]);

  const selectedStall = stalls.find((s) => String(s.id) === String(selectedStallId));

  const handleAssignment = async (event) => {
    event.preventDefault();
    if (!selectedStallId || !assignmentEmail.trim()) {
      toast.error('Please select a stall and enter a staff email.');
      return;
    }

    try {
      setAssignmentLoading(true);
      setAssignmentError('');
      setAssignmentSuccess('');
      await assignStaffToStall(selectedStallId, { staffEmail: assignmentEmail.trim() });
      toast.success('Staff assigned successfully.');
      setAssignmentSuccess('Staff assigned successfully.');
      setAssignmentEmail('');
      await fetchAssignments(selectedStallId);
    } catch (assignError) {
      const message = assignError.response?.data?.message || 'Failed to assign staff';
      setAssignmentError(message);
      toast.error(message);
    } finally {
      setAssignmentLoading(false);
    }
  };

  const handleRemoveAssignment = async (staffId) => {
    if (!selectedStallId) {
      return;
    }

    try {
      setAssignmentLoading(true);
      await removeStaffFromStall(selectedStallId, staffId);
      toast.success('Staff removed from stall.');
      await fetchAssignments(selectedStallId);
    } catch (removeError) {
      const message = removeError.response?.data?.message || 'Failed to remove staff assignment';
      setAssignmentError(message);
      toast.error(message);
    } finally {
      setAssignmentLoading(false);
    }
  };

  const onStallSelectionChange = (event) => {
    setSelectedStallId(event.target.value);
    setAssignmentSuccess('');
    setAssignmentError('');
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  return (
    <div className="container-custom py-8">
      <div className="mb-6 flex items-start justify-between gap-4">
        <div>
          <h1 className="text-4xl font-bold text-gray-900">Admin Stall Management</h1>
          <p className="text-gray-600 mt-2">
            Create, update, and remove any stall in the system.
          </p>
        </div>
        <Link to="/staff/dashboard" className="btn-secondary">
          Back to Admin Dashboard
        </Link>
      </div>

      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg flex items-start gap-3">
          <AlertCircle className="w-5 h-5 text-red-600 mt-0.5 flex-shrink-0" />
          <p className="text-red-700">{error}</p>
        </div>
      )}

      <div className="grid lg:grid-cols-[1fr_1.2fr] gap-6">
        <div className="card">
          <div className="flex items-center gap-3 mb-6">
            <div className="w-12 h-12 rounded-xl bg-blue-100 text-blue-700 flex items-center justify-center">
              <PlusCircle size={24} />
            </div>
            <div>
              <h2 className="text-2xl font-semibold text-gray-900">
                {editingId ? 'Edit Stall' : 'Create Stall'}
              </h2>
              <p className="text-sm text-gray-600">
                Use this form to manage stall details.
              </p>
            </div>
          </div>

          {successMessage && (
            <div className="mb-4 rounded-lg border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700">
              {successMessage}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="form-group">
              <label htmlFor="stallName" className="form-label">Stall Name</label>
              <input
                id="stallName"
                className="input-field"
                value={form.stallName}
                onChange={updateField('stallName')}
                placeholder="e.g. Rice Bowl"
                disabled={submitting}
              />
              {errors.stallName && <p className="mt-2 text-sm text-red-600">{errors.stallName}</p>}
            </div>

            <div className="form-group">
              <label htmlFor="vendorName" className="form-label">Vendor Name</label>
              <input
                id="vendorName"
                className="input-field"
                value={form.vendorName}
                onChange={updateField('vendorName')}
                placeholder="e.g. Demo Vendor"
                disabled={submitting}
              />
              {errors.vendorName && <p className="mt-2 text-sm text-red-600">{errors.vendorName}</p>}
            </div>

            <div className="form-group">
              <label htmlFor="operatingHours" className="form-label">Operating Hours</label>
              <input
                id="operatingHours"
                className="input-field"
                value={form.operatingHours}
                onChange={updateField('operatingHours')}
                placeholder="e.g. 8:00 AM - 2:00 PM"
                disabled={submitting}
              />
              {errors.operatingHours && (
                <p className="mt-2 text-sm text-red-600">{errors.operatingHours}</p>
              )}
            </div>

            <div className="flex gap-3">
              <button type="submit" className="btn-primary flex-1 flex items-center justify-center gap-2" disabled={submitting}>
                {submitting ? <Loader2 className="h-4 w-4 animate-spin" /> : <PenLine className="h-4 w-4" />}
                {editingId ? 'Update Stall' : 'Create Stall'}
              </button>
              {editingId && (
                <button type="button" className="btn-secondary" onClick={resetForm} disabled={submitting}>
                  Cancel
                </button>
              )}
            </div>
          </form>
        </div>

        <div className="space-y-4">
          {stalls.length === 0 ? (
            <div className="card text-center py-12">
              <p className="text-gray-500 text-lg">No stalls found</p>
            </div>
          ) : (
            stalls.map((stall) => (
              <div key={stall.id} className="card">
                <div className="flex items-start justify-between gap-4">
                  <div>
                    <h3 className="text-xl font-semibold text-gray-900">{stall.stallName}</h3>
                    <p className="text-sm text-gray-600 mt-1">{stall.vendorName}</p>
                    <p className="text-sm text-gray-600">Hours: {stall.operatingHours || 'Not specified'}</p>
                    <p className="text-sm text-gray-600">Queue limit: {stall.queueLimit ?? 100}</p>
                  </div>
                  <div className="flex gap-2">
                    <button
                      type="button"
                      onClick={() => startEdit(stall)}
                      className="btn-secondary flex items-center gap-2"
                    >
                      <PenLine size={16} />
                      Edit
                    </button>
                    <button
                      type="button"
                      onClick={() => handleDelete(stall.id)}
                      disabled={deletingId === stall.id}
                      className="btn-danger flex items-center gap-2"
                    >
                      {deletingId === stall.id ? (
                        <Loader2 size={16} className="animate-spin" />
                      ) : (
                        <Trash2 size={16} />
                      )}
                      Delete
                    </button>
                  </div>
                </div>
              </div>
            ))
          )}
        </div>
      </div>

      <div className="mt-8 card">
        <div className="mb-6 flex items-start justify-between gap-4">
          <div>
            <h2 className="text-2xl font-semibold text-gray-900">Staff Assignment</h2>
            <p className="text-sm text-gray-600">
              Assign and remove staff from a selected stall by Firebase email.
            </p>
          </div>
          <div className="flex items-center gap-3">
            <label htmlFor="selectedStall" className="text-sm font-medium text-gray-700">
              Stall
            </label>
            <select
              id="selectedStall"
              value={selectedStallId}
              onChange={onStallSelectionChange}
              className="input-field max-w-xs"
            >
              <option value="">Select a stall</option>
              {stalls.map((stall) => (
                <option key={stall.id} value={stall.id}>
                  {stall.stallName}
                </option>
              ))}
            </select>
          </div>
        </div>

        {assignmentError && (
          <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {assignmentError}
          </div>
        )}

        {assignmentSuccess && (
          <div className="mb-4 rounded-lg border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700">
            {assignmentSuccess}
          </div>
        )}

        <form onSubmit={handleAssignment} className="grid gap-4 sm:grid-cols-[1.6fr_1fr] items-end">
          <div className="form-group">
            <label htmlFor="assignmentEmail" className="form-label">Staff Firebase Email</label>
            <input
              id="assignmentEmail"
              className="input-field"
              value={assignmentEmail}
              onChange={(event) => setAssignmentEmail(event.target.value)}
              placeholder="staff@example.com"
              disabled={assignmentLoading || !selectedStallId}
            />
          </div>
          <button
            type="submit"
            className="btn-primary flex items-center justify-center gap-2"
            disabled={assignmentLoading || !selectedStallId}
          >
            {assignmentLoading ? <Loader2 className="h-4 w-4 animate-spin" /> : <PlusCircle size={16} />}
            Assign Staff
          </button>
        </form>

        <div className="mt-6">
          <h3 className="text-lg font-semibold text-gray-900">Current Assignments</h3>
          {loadingAssignments ? (
            <div className="mt-4 text-sm text-gray-600 flex items-center gap-2">
              <Loader2 className="h-4 w-4 animate-spin" /> Loading assignments...
            </div>
          ) : assignments.length === 0 ? (
            <p className="mt-4 text-sm text-gray-600">No staff currently assigned to this stall.</p>
          ) : (
            <div className="mt-4 space-y-3">
              {assignments.map((assignment) => (
                <div key={assignment.assignmentId ?? assignment.staffId} className="rounded-xl border border-gray-200 bg-white p-4 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
                  <div>
                    <p className="text-sm font-medium text-gray-900">{assignment.staffEmail}</p>
                    <p className="text-sm text-gray-600">{assignment.staffDisplayName || 'Unknown name'}</p>
                    <p className="text-sm text-gray-600">Assigned Stall: {selectedStall?.stallName || '—'}</p>
                  </div>
                  <button
                    type="button"
                    className="btn-secondary min-w-[160px]"
                    onClick={() => handleRemoveAssignment(assignment.staffId)}
                    disabled={assignmentLoading}
                  >
                    Remove Assignment
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      <div className="mt-6 card bg-blue-50 border border-blue-200">
        <div className="flex items-start gap-3">
          <RotateCcw className="w-5 h-5 text-blue-700 mt-0.5" />
          <div>
            <h3 className="font-semibold text-blue-900 mb-1">Admin Scope</h3>
            <p className="text-blue-800 text-sm">
              This page manages every stall in the system. Menu item edits stay on the dashboard for the assigned stall only.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default StaffAdminPage;
