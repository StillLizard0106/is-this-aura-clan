import React, { useState, useEffect } from 'react';
import { ShoppingCart, Loader2, AlertCircle } from 'lucide-react';
import { getStalls } from '../../api/endpoints';
import { formatCurrency } from '../../utils/currencyFormatter';
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
    <div className="container-custom py-8">
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-gray-900 mb-2">Browse Stalls</h1>
        <p className="text-gray-600">Select a stall to place your order</p>
      </div>

      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg flex items-start gap-3">
          <AlertCircle className="w-5 h-5 text-red-600 mt-0.5 flex-shrink-0" />
          <div>
            <h3 className="font-semibold text-red-900">Error</h3>
            <p className="text-red-700 text-sm">{error}</p>
          </div>
        </div>
      )}

      {stalls.length === 0 ? (
        <div className="card text-center py-12">
          <ShoppingCart className="w-16 h-16 text-gray-300 mx-auto mb-4" />
          <h2 className="text-xl font-semibold text-gray-600 mb-2">
            No stalls available
          </h2>
          <p className="text-gray-500">
            Check back later for food stalls to open.
          </p>
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
  );
};

export default StudentDashboardPage;
