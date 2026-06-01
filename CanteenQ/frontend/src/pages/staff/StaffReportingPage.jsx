import React, { useState, useEffect } from 'react';
import { AlertCircle } from 'lucide-react';
import { getReportingSummary } from '../../api/endpoints';
import { formatCurrency } from '../../utils/currencyFormatter';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { toast } from 'react-toastify';

const StaffReportingPage = () => {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  useEffect(() => {
    fetchSummary();
  }, []);

  const fetchSummary = async (start = '', end = '') => {
    try {
      setLoading(true);
      const params = {};
      if (start) params.startDate = start;
      if (end) params.endDate = end;
      const response = await getReportingSummary(params);
      setSummary(response.data);
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'Failed to load reporting summary';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleDateFilter = () => {
    fetchSummary(startDate, endDate);
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  return (
    <div className="container-custom py-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-6">Daily Reporting</h1>

      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg flex items-start gap-3">
          <AlertCircle className="w-5 h-5 text-red-600 mt-0.5 flex-shrink-0" />
          <p className="text-red-700">{error}</p>
        </div>
      )}

      {/* Date Filters */}
      <div className="card mb-6">
        <h2 className="font-semibold text-gray-900 mb-4">Filter by Date Range</h2>
        <div className="grid md:grid-cols-3 gap-4">
          <div className="form-group">
            <label className="form-label">Start Date</label>
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              className="input-field"
            />
          </div>
          <div className="form-group">
            <label className="form-label">End Date</label>
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              className="input-field"
            />
          </div>
          <div className="flex items-end">
            <button onClick={handleDateFilter} className="btn-primary w-full">
              Filter
            </button>
          </div>
        </div>
      </div>

      {summary && (
        <>
          {/* Summary Statistics */}
          <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
            <div className="card bg-gradient-to-br from-blue-50 to-blue-100">
              <h3 className="text-gray-600 text-sm font-semibold mb-2">Total Orders</h3>
              <p className="text-4xl font-bold text-blue-600">
                {summary.totalOrders || 0}
              </p>
            </div>

            <div className="card bg-gradient-to-br from-green-50 to-green-100">
              <h3 className="text-gray-600 text-sm font-semibold mb-2">Completed Orders</h3>
              <p className="text-4xl font-bold text-green-600">
                {summary.ordersCompleted || 0}
              </p>
            </div>

            <div className="card bg-gradient-to-br from-yellow-50 to-yellow-100">
              <h3 className="text-gray-600 text-sm font-semibold mb-2">Unclaimed Orders</h3>
              <p className="text-4xl font-bold text-yellow-600">
                {summary.ordersUnclaimed || 0}
              </p>
            </div>

          <div className="card bg-gradient-to-br from-purple-50 to-purple-100">
            <h3 className="text-gray-600 text-sm font-semibold mb-2">Total Revenue</h3>
            <p className="text-2xl font-bold text-purple-600">
              {formatCurrency(summary.totalRevenue ?? 0)}
            </p>
          </div>
        </div>

          {/* Per-Stall Breakdown */}
          {summary.stallBreakdowns && summary.stallBreakdowns.length > 0 && (
            <div className="card">
              <h2 className="text-2xl font-semibold text-gray-900 mb-4">
                Per-Stall Breakdown
              </h2>
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead className="bg-gray-100 border-b-2 border-gray-300">
                    <tr>
                      <th className="px-4 py-2 text-left">Stall Name</th>
                      <th className="px-4 py-2 text-right">Orders</th>
                      <th className="px-4 py-2 text-right">Revenue</th>
                    </tr>
                  </thead>
                  <tbody>
                    {summary.stallBreakdowns.map((stall, idx) => (
                      <tr key={idx} className="border-b hover:bg-gray-50">
                        <td className="px-4 py-3 font-semibold text-gray-900">
                          {stall.stallName}
                        </td>
                        <td className="px-4 py-3 text-right text-gray-700">
                          {stall.totalOrders}
                        </td>
                        <td className="px-4 py-3 text-right font-semibold text-green-600">
                          {formatCurrency(stall.totalRevenue ?? 0)}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default StaffReportingPage;
