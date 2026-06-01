import React, { useState, useEffect } from 'react';
import { ShoppingCart, AlertCircle, Sparkles, ArrowRight } from 'lucide-react';
import { getStalls } from '../../api/endpoints';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';

const StallCard = ({ stall, onSelectStall }) => {
  return (
    <div
      onClick={() => onSelectStall(stall.id)}
      className="card-hover"
    >
      <h3 className="text-xl font-semibold text-gray-800 mb-2">
        {stall.stallName}
      </h3>
      <p className="text-gray-600 text-sm mb-3">Vendor: {stall.vendorName}</p>
      <p className="text-gray-500 text-xs mb-4">
        Hours: {stall.operatingHours || 'Not specified'}
      </p>
      <p className="text-gray-500 text-xs mb-4">
        Queue: {Math.max((stall.queueLimit ?? 100) - (stall.queueSlotsLeft ?? 100), 0)}/{stall.queueLimit ?? 100}
      </p>
      <p className="text-gray-500 text-xs mb-4">
        {stall.queueSlotsLeft ?? 100} slots left
      </p>
      <button className="btn-primary w-full">Browse Menu</button>
    </div>
  );
};

const StudentDashboardPage = () => {
  const [stalls, setStalls] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchStalls = async () => {
      try {
        setLoading(true);
        const response = await getStalls();
        setStalls(response.data);
      } catch (err) {
        const errorMessage = err.response?.data?.message || 'Failed to load stalls';
        setError(errorMessage);
        toast.error(errorMessage);
      } finally {
        setLoading(false);
      }
    };

    fetchStalls();
  }, []);

  if (loading) {
    return <LoadingSpinner />;
  }

  return (
    <div
      className="min-h-screen py-8"
      style={{
        background:
          'radial-gradient(circle at top right, rgba(245,168,0,0.12), transparent 24%), radial-gradient(circle at bottom left, rgba(37,99,235,0.10), transparent 26%), linear-gradient(180deg, #f8fbff 0%, #eef4ff 100%)',
      }}
    >
      <div className="container-custom">
        <div className="mb-8 rounded-[28px] border border-white/70 bg-white/80 p-6 shadow-[0_18px_50px_rgba(15,23,42,0.08)] backdrop-blur-xl">
          <div className="flex flex-col gap-5 md:flex-row md:items-end md:justify-between">
            <div>
              <p className="mb-3 inline-flex items-center gap-2 rounded-full bg-blue-50 px-3 py-1 text-xs font-semibold uppercase tracking-[0.28em] text-blue-700">
                <Sparkles size={14} />
                Student Dashboard
              </p>
              <h1 className="text-4xl font-bold tracking-tight text-slate-950 md:text-5xl">
                Browse stalls
              </h1>
              <p className="mt-3 max-w-2xl text-sm leading-6 text-slate-600 md:text-base">
                Choose a stall, check queue pressure, and jump straight into ordering.
              </p>
            </div>
            <div className="grid gap-3 rounded-[24px] bg-slate-950 px-5 py-4 text-white shadow-[0_14px_36px_rgba(15,23,42,0.18)]">
              <p className="text-[11px] font-semibold uppercase tracking-[0.22em] text-slate-300">Quick Start</p>
              <p className="text-sm text-slate-200">Tap any stall card to browse its menu.</p>
              <div className="flex items-center gap-2 text-xs font-semibold text-amber-300">
                <ArrowRight size={14} />
                Fresh stalls update as the backend syncs
              </div>
            </div>
          </div>
        </div>

        {error && (
          <div className="mb-6 flex items-start gap-3 rounded-2xl border border-red-200 bg-red-50 p-4 shadow-sm">
            <AlertCircle className="mt-0.5 h-5 w-5 flex-shrink-0 text-red-600" />
            <div>
              <h3 className="font-semibold text-red-900">Error</h3>
              <p className="text-sm text-red-700">{error}</p>
            </div>
          </div>
        )}

        {stalls.length === 0 ? (
          <div className="rounded-[28px] border border-white/70 bg-white/85 py-14 text-center shadow-[0_18px_50px_rgba(15,23,42,0.08)] backdrop-blur-xl">
            <ShoppingCart className="mx-auto mb-4 h-16 w-16 text-blue-200" />
            <h2 className="mb-2 text-xl font-semibold text-slate-700">No stalls available</h2>
            <p className="text-slate-500">Check back later for food stalls to open.</p>
          </div>
        ) : (
          <div className="grid-auto-fit">
            {stalls.map((stall) => (
              <StallCard
                key={stall.id}
                stall={stall}
                onSelectStall={(stallId) =>
                  navigate(`/stall/${stallId}/menu`)
                }
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default StudentDashboardPage;
