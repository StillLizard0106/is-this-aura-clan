import React, { useState, useEffect } from 'react';
import { AlertCircle, CalendarRange, BarChart3 } from 'lucide-react';
import { getReportingDaily, getReportingStalls } from '../../api/endpoints';
import { formatCurrency } from '../../utils/currencyFormatter';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { toast } from 'react-toastify';

const StaffReportingPage = () => {
  const [daily, setDaily] = useState(null);
  const [stalls, setStalls] = useState([]);
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
      const [dailyResp, stallsResp] = await Promise.all([
        getReportingDaily(params),
        getReportingStalls(params)
      ]);
      setDaily(dailyResp.data);
      setStalls(stallsResp.data || []);
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
    <div
      className="min-h-screen py-8"
      style={{
        background:
          'radial-gradient(circle at top left, rgba(37,99,235,0.16), transparent 22%), radial-gradient(circle at top right, rgba(8,21,53,0.12), transparent 24%), linear-gradient(180deg, #f7fbff 0%, #eef4ff 100%)',
      }}
    >
      <div className="container-custom">
        <div className="mb-8 rounded-[28px] border border-white/70 bg-white/80 p-6 shadow-[0_18px_50px_rgba(15,23,42,0.08)] backdrop-blur-xl">
          <p className="mb-3 inline-flex items-center gap-2 rounded-full bg-blue-50 px-3 py-1 text-xs font-semibold uppercase tracking-[0.28em] text-blue-700">
            <BarChart3 size={14} />
            Staff Reporting
          </p>
          <h1 className="text-4xl font-bold tracking-tight text-slate-950 md:text-5xl">Daily Reporting</h1>
          <p className="mt-3 max-w-2xl text-sm leading-6 text-slate-600 md:text-base">
            Filter by date range and review totals, completion counts, and stall performance.
          </p>
        </div>

        {error && (
          <div className="mb-6 flex items-start gap-3 rounded-2xl border border-red-200 bg-red-50 p-4 shadow-sm">
            <AlertCircle className="mt-0.5 h-5 w-5 flex-shrink-0 text-red-600" />
            <p className="text-red-700">{error}</p>
          </div>
        )}

        <div className="mb-6 rounded-[28px] border border-white/70 bg-white/85 p-6 shadow-[0_18px_50px_rgba(15,23,42,0.08)] backdrop-blur-xl">
          <h2 className="mb-4 flex items-center gap-2 text-xl font-semibold text-slate-950">
            <CalendarRange size={18} />
            Filter by Date Range
          </h2>
          <div className="grid gap-4 md:grid-cols-3">
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
              <button
                onClick={handleDateFilter}
                className="w-full rounded-xl bg-slate-950 px-4 py-3 text-sm font-semibold text-white transition hover:bg-slate-800"
              >
                Filter
              </button>
            </div>
          </div>
        </div>

        {daily && (
          <>
            <div className="mb-8 grid gap-4 md:grid-cols-2 lg:grid-cols-4">
              <div className="rounded-[24px] border border-white/70 bg-gradient-to-br from-blue-50 to-blue-100 p-6 shadow-[0_18px_50px_rgba(15,23,42,0.08)]">
                <h3 className="mb-2 text-sm font-semibold uppercase tracking-[0.24em] text-slate-500">Total Orders</h3>
                <p className="text-4xl font-bold tracking-tight text-blue-600">{daily.totalOrders || 0}</p>
              </div>

              <div className="rounded-[24px] border border-white/70 bg-gradient-to-br from-emerald-50 to-emerald-100 p-6 shadow-[0_18px_50px_rgba(15,23,42,0.08)]">
                <h3 className="mb-2 text-sm font-semibold uppercase tracking-[0.24em] text-slate-500">Completed Orders</h3>
                <p className="text-4xl font-bold tracking-tight text-emerald-600">{daily.completedToday || 0}</p>
              </div>

              <div className="rounded-[24px] border border-white/70 bg-gradient-to-br from-blue-50 to-sky-100 p-6 shadow-[0_18px_50px_rgba(15,23,42,0.08)]">
                <h3 className="mb-2 text-sm font-semibold uppercase tracking-[0.24em] text-slate-500">Unclaimed Orders</h3>
                <p className="text-4xl font-bold tracking-tight text-blue-700">{daily.unclaimedToday || 0}</p>
              </div>

              <div className="rounded-[24px] border border-white/70 bg-gradient-to-br from-slate-50 to-blue-100 p-6 shadow-[0_18px_50px_rgba(15,23,42,0.08)]">
                <h3 className="mb-2 text-sm font-semibold uppercase tracking-[0.24em] text-slate-500">Total Revenue</h3>
                <p className="text-2xl font-bold tracking-tight text-slate-900">
                  {formatCurrency(daily.totalRevenue ?? 0)}
                </p>
              </div>
            </div>
            {stalls && stalls.length > 0 && (
              <div className="overflow-hidden rounded-[28px] border border-white/70 bg-white/90 shadow-[0_18px_50px_rgba(15,23,42,0.08)] backdrop-blur-xl">
                <div className="border-b border-slate-200 px-6 py-5">
                  <h2 className="text-2xl font-semibold text-slate-950">Per-Stall Breakdown</h2>
                </div>
                <div className="overflow-x-auto">
                  <table className="w-full">
                    <thead className="bg-slate-950 text-white">
                      <tr>
                        <th className="px-6 py-4 text-left text-sm font-semibold">Stall Name</th>
                        <th className="px-6 py-4 text-right text-sm font-semibold">Orders</th>
                        <th className="px-6 py-4 text-right text-sm font-semibold">Revenue</th>
                      </tr>
                    </thead>
                    <tbody>
                      {stalls.map((stall, idx) => (
                        <tr key={idx} className="border-b border-slate-100 hover:bg-slate-50/80">
                          <td className="px-6 py-4 font-semibold text-slate-950">{stall.stallName}</td>
                          <td className="px-6 py-4 text-right text-slate-700">{stall.orderCount || stall.totalOrders}</td>
                          <td className="px-6 py-4 text-right font-semibold text-emerald-600">
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
    </div>
  );
};

export default StaffReportingPage;
