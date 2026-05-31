import React, { useState, useEffect } from 'react';
import { AlertCircle, Loader2 } from 'lucide-react';
import { getStaffDashboard } from '../../api/endpoints';
import { useAuth } from '../../hooks/useAuth';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { toast } from 'react-toastify';

const StaffDashboardPage = () => {
  const { user, userRole } = useAuth();
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        setLoading(true);
        const response = await getStaffDashboard();
        setDashboard(response.data);
      } catch (err) {
        const errorMessage = err.response?.data?.message || 'Failed to load staff dashboard';
        setError(errorMessage);
        toast.error(errorMessage);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboard();
  }, []);

  if (loading) {
    return <LoadingSpinner />;
  }

  if (error) {
    return (
      <div className="container-custom py-8">
        <div className="card p-6 bg-red-50 border border-red-200">
          <AlertCircle className="w-8 h-8 text-red-600 mb-2" />
          <h2 className="text-xl font-semibold text-red-900 mb-2">
            Access Denied
          </h2>
          <p className="text-red-700">{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="container-custom py-8">
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-gray-900">Staff Dashboard</h1>
        <p className="text-gray-600 mt-2">Welcome, {user?.email}</p>
      </div>

      <div className="grid md:grid-cols-2 gap-6 mb-8">
        <div className="card bg-gradient-to-br from-blue-50 to-blue-100">
          <h3 className="text-gray-600 text-sm font-semibold mb-2">Logged In As</h3>
          <p className="text-3xl font-bold text-gray-900">{user?.displayName || 'Staff'}</p>
          <p className="text-gray-600 text-sm mt-1">{user?.email}</p>
        </div>

        <div className="card bg-gradient-to-br from-green-50 to-green-100">
          <h3 className="text-gray-600 text-sm font-semibold mb-2">Role</h3>
          <p className="text-3xl font-bold text-green-700">{userRole}</p>
          <p className="text-gray-600 text-sm mt-1">Staff Member</p>
        </div>
      </div>

      <div className="grid md:grid-cols-2 gap-6">
        <div className="card">
          <h3 className="text-xl font-semibold text-gray-900 mb-4">Quick Actions</h3>
          <div className="space-y-2">
            <a
              href="/staff/orders"
              className="block btn-primary text-center"
            >
              View Order Queue
            </a>
            <a
              href="/staff/reporting"
              className="block btn-secondary text-center"
            >
              View Daily Report
            </a>
          </div>
        </div>

        <div className="card">
          <h3 className="text-xl font-semibold text-gray-900 mb-4">System Status</h3>
          <div className="space-y-2">
            <div className="flex justify-between items-center">
              <span className="text-gray-600">Backend Connection</span>
              <span className="badge badge-success">Connected</span>
            </div>
            <div className="flex justify-between items-center">
              <span className="text-gray-600">Database</span>
              <span className="badge badge-success">OK</span>
            </div>
          </div>
        </div>
      </div>

      <div className="mt-8 card bg-blue-50 border border-blue-200">
        <h3 className="font-semibold text-blue-900 mb-2">About CanteenQ</h3>
        <p className="text-blue-800 text-sm">
          CanteenQ is a digital pre-ordering system for school canteens. As a staff member,
          you can manage incoming orders, update order statuses, and view daily reporting summaries.
        </p>
      </div>
    </div>
  );
};

export default StaffDashboardPage;
