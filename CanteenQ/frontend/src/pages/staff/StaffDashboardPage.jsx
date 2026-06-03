import React, { useState, useEffect } from 'react';
import { AlertCircle, Loader2, ShieldCheck, ArrowRight, UtensilsCrossed } from 'lucide-react';
import { Link } from 'react-router-dom';
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
      <div
        className="min-h-screen py-8"
        style={{
          background:
            'radial-gradient(circle at top left, rgba(239,68,68,0.10), transparent 24%), linear-gradient(180deg, #fff8f8 0%, #f4f7ff 100%)',
        }}
      >
        <div className="container-custom">
          <div className="rounded-[28px] border border-red-200 bg-red-50 p-6 shadow-sm">
            <AlertCircle className="mb-2 h-8 w-8 text-red-600" />
            <h2 className="mb-2 text-xl font-semibold text-red-900">Access Denied</h2>
            <p className="text-red-700">{error}</p>
          </div>
        </div>
      </div>
    );
  }

  const isAdmin = userRole === 'ADMIN';

  return (
      <div
        className="min-h-screen py-8"
        style={{
          background:
            'radial-gradient(circle at top right, rgba(37,99,235,0.16), transparent 22%), radial-gradient(circle at bottom left, rgba(8,21,53,0.12), transparent 26%), linear-gradient(180deg, #f7fbff 0%, #eef4ff 100%)',
        }}
      >
      <div className="container-custom">
        <div className="mb-8 rounded-[28px] border border-white/70 bg-white/80 p-6 shadow-[0_18px_50px_rgba(15,23,42,0.08)] backdrop-blur-xl">
          <div className="flex flex-col gap-5 md:flex-row md:items-end md:justify-between">
            <div>
              <p className="mb-3 inline-flex items-center gap-2 rounded-full bg-blue-50 px-3 py-1 text-xs font-semibold uppercase tracking-[0.28em] text-blue-700">
                <ShieldCheck size={14} />
                {isAdmin ? 'Admin Console' : 'Staff Console'}
              </p>
              <h1 className="text-4xl font-bold tracking-tight text-slate-950 md:text-5xl">
                {isAdmin ? 'Admin Dashboard' : 'Staff Dashboard'}
              </h1>
              <p className="mt-3 max-w-2xl text-sm leading-6 text-slate-600 md:text-base">
                {isAdmin
                  ? 'Overview of the logged-in admin account and access to stall management tools.'
                  : 'Overview of the logged-in staff account, quick access to queue control, and reporting tools.'}
              </p>
            </div>
            <div className="rounded-[24px] bg-slate-950 px-5 py-4 text-white shadow-[0_14px_36px_rgba(15,23,42,0.18)]">
              <p className="text-[11px] font-semibold uppercase tracking-[0.22em] text-slate-300">Signed in</p>
              <p className="mt-1 text-lg font-semibold">{user?.displayName || (isAdmin ? 'Admin' : 'Staff')}</p>
              <p className="text-sm text-slate-300">{user?.email}</p>
            </div>
          </div>
        </div>

        <div className="grid gap-6 md:grid-cols-2 mb-8">
          <div className="rounded-[28px] border border-white/70 bg-white/85 p-6 shadow-[0_18px_50px_rgba(15,23,42,0.08)] backdrop-blur-xl">
            <h3 className="mb-2 text-sm font-semibold uppercase tracking-[0.24em] text-slate-500">Logged In As</h3>
            <p className="text-3xl font-bold tracking-tight text-slate-950">{user?.displayName || 'Staff'}</p>
            <p className="mt-1 text-sm text-slate-500">{user?.email}</p>
          </div>

          <div className="rounded-[28px] border border-blue-100 bg-gradient-to-br from-blue-50 to-indigo-50 p-6 shadow-[0_18px_50px_rgba(15,23,42,0.08)] backdrop-blur-xl">
            <h3 className="mb-2 text-sm font-semibold uppercase tracking-[0.24em] text-slate-500">Role</h3>
            <p className="text-3xl font-bold tracking-tight text-blue-700">{userRole}</p>
            <p className="mt-1 text-sm text-slate-500">Staff member</p>
          </div>
        </div>

        <div className="grid gap-6 lg:grid-cols-[1fr_1fr]">
          <div className="rounded-[28px] border border-white/70 bg-white/85 p-6 shadow-[0_18px_50px_rgba(15,23,42,0.08)] backdrop-blur-xl">
            <h3 className="mb-4 text-xl font-semibold text-slate-950">
              {isAdmin ? 'Admin Controls' : 'Quick Actions'}
            </h3>
            {isAdmin ? (
              <div className="space-y-4">
                <p className="text-sm text-slate-600">
                  Use the admin stall management console to create stalls, assign staff, and manage access.
                </p>
                <Link
                  to="/staff/admin"
                  className="inline-flex items-center justify-center rounded-2xl bg-slate-950 px-5 py-4 text-white transition hover:bg-slate-800"
                >
                  <span>Go to Stall Management</span>
                  <ArrowRight size={16} className="ml-2" />
                </Link>
              </div>
            ) : (
              <div className="space-y-3">
                <a href="/staff/orders" className="flex items-center justify-between rounded-2xl bg-slate-950 px-4 py-4 text-white transition hover:bg-slate-800">
                  <span>View Order Queue</span>
                  <ArrowRight size={16} />
                </a>
                <a href="/staff/menu" className="flex items-center justify-between rounded-2xl border border-amber-200 bg-gradient-to-r from-amber-50 to-orange-50 px-4 py-4 text-slate-800 transition hover:bg-amber-100">
                  <span className="flex items-center gap-2">
                    <UtensilsCrossed size={16} />
                    Edit Menu Items
                  </span>
                  <ArrowRight size={16} />
                </a>
                <a href="/staff/reporting" className="flex items-center justify-between rounded-2xl border border-slate-200 bg-white px-4 py-4 text-slate-800 transition hover:bg-slate-50">
                  <span>View Daily Report</span>
                  <ArrowRight size={16} />
                </a>
              </div>
            )}
          </div>

          <div className="rounded-[28px] border border-white/70 bg-white/85 p-6 shadow-[0_18px_50px_rgba(15,23,42,0.08)] backdrop-blur-xl">
            <h3 className="mb-4 text-xl font-semibold text-slate-950">System Status</h3>
            <div className="space-y-3">
              <div className="flex items-center justify-between rounded-2xl bg-blue-50 px-4 py-3">
                <span className="text-slate-600">Backend connection</span>
                <span className="rounded-full bg-blue-600 px-3 py-1 text-xs font-semibold text-white">Connected</span>
              </div>
              <div className="flex items-center justify-between rounded-2xl bg-blue-50 px-4 py-3">
                <span className="text-slate-600">Database</span>
                <span className="rounded-full bg-blue-600 px-3 py-1 text-xs font-semibold text-white">OK</span>
              </div>
            </div>
          </div>
        </div>

        <div className="mt-8 rounded-[28px] border border-blue-100 bg-gradient-to-r from-blue-50 to-sky-50 p-6 shadow-[0_18px_50px_rgba(15,23,42,0.06)]">
          <h3 className="mb-2 text-lg font-semibold text-slate-950">About CanteenQ</h3>
          <p className="text-sm leading-6 text-slate-600">
            CanteenQ is a digital pre-ordering system for school canteens. As a staff member,
            you can manage incoming orders, update statuses, and view reporting summaries from the console.
          </p>
        </div>
      </div>
    </div>
  );
};

export default StaffDashboardPage;
