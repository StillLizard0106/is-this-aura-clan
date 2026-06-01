import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { CheckCircle, Clock, MapPin } from 'lucide-react';
import { getOrderDetail } from '../../api/endpoints';
import { formatDateTime, getTimeRemaining } from '../../utils/dateFormatter';
import { formatCurrency } from '../../utils/currencyFormatter';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { AlertCircle } from 'lucide-react';
import { toast } from 'react-toastify';

const OrderConfirmationPage = () => {
  const { orderId } = useParams();
  const navigate = useNavigate();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let isMounted = true;

    const fetchOrder = async () => {
      try {
        setLoading(true);
        const response = await getOrderDetail(orderId);
        if (!isMounted) {
          return;
        }
        setOrder(response.data);
      } catch (err) {
        if (!isMounted) {
          return;
        }
        const errorMessage = err.response?.data?.message || 'Failed to load order details';
        setError(errorMessage);
        toast.error(errorMessage);
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };

    fetchOrder();

    return () => {
      isMounted = false;
    };
  }, [orderId]);

  if (loading) {
    return <LoadingSpinner />;
  }

  if (error || !order) {
    return (
      <div className="container-custom py-12 min-h-screen flex items-center justify-center">
        <div className="card max-w-md w-full text-center">
          <AlertCircle className="w-16 h-16 text-red-500 mx-auto mb-4" />
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Order Not Found</h1>
          <p className="text-gray-600 mb-6">{error || 'Unable to load the order confirmation.'}</p>
          <button
            onClick={() => navigate('/orders')}
            className="btn-primary w-full"
          >
            View Orders
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="container-custom py-12 min-h-screen flex items-center justify-center">
      <div className="card max-w-md w-full text-center">
        <CheckCircle className="w-16 h-16 text-green-500 mx-auto mb-4" />
        <h1 className="text-3xl font-bold text-gray-900 mb-2">Order Placed!</h1>
        <p className="text-gray-600 mb-6">Your order has been successfully placed.</p>

        <div className="bg-blue-50 rounded-lg p-4 mb-6 text-left">
          <div className="mb-4">
            <p className="text-sm text-gray-600">Order ID</p>
            <p className="text-lg font-mono font-semibold text-gray-900 break-all">
              {order.id}
            </p>
          </div>
          <div className="mb-4">
            <p className="text-sm text-gray-600">Queue Number</p>
            <p className="text-4xl font-bold text-blue-600">{order.queueNumber}</p>
          </div>
          <div className="mb-4">
            <p className="text-sm text-gray-600 flex items-center gap-1">
              <Clock size={16} /> Pickup Time
            </p>
            <p className="font-semibold text-gray-900">
              {formatDateTime(order.pickupSlot)}
            </p>
          </div>
          <div>
            <p className="text-sm text-gray-600">Total Amount</p>
            <p className="text-2xl font-bold text-blue-600">
              {formatCurrency(order.totalPrice)}
            </p>
          </div>
        </div>

        <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4 mb-6 text-sm text-left">
          <p className="font-semibold text-yellow-900 mb-2">Important:</p>
          <ul className="text-yellow-800 space-y-1 text-xs">
            <li>• Pick up your order by <strong>{formatDateTime(order.pickupSlot)}</strong></li>
            <li>• Your queue number is <strong>{order.queueNumber}</strong></li>
            <li>• You will receive notifications when your order is ready</li>
            <li>• Unclaimed orders expire 10 minutes after pickup time</li>
          </ul>
        </div>

        <button
          onClick={() => navigate('/orders')}
          className="btn-primary w-full mb-2"
        >
          View Order Details
        </button>
        <button
          onClick={() => navigate('/dashboard')}
          className="btn-secondary w-full"
        >
          Continue Shopping
        </button>
      </div>
    </div>
  );
};

export default OrderConfirmationPage;
