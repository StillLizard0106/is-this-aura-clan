import React, { useState, useEffect } from 'react';
import { Clock, AlertCircle, Loader2 } from 'lucide-react';
import { getMyOrders, subscribeToOrderUpdates } from '../../api/endpoints';
import StatusBadge from '../../components/shared/StatusBadge';
import { formatDateTime, getTimeRemaining } from '../../utils/dateFormatter';
import { formatCurrency } from '../../utils/currencyFormatter';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { toast } from 'react-toastify';

const OrderHistoryPage = () => {
  const [activeOrders, setActiveOrders] = useState([]);
  const [pastOrders, setPastOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        setLoading(true);
        const response = await getMyOrders();
        setActiveOrders(response.data.activeOrders || []);
        setPastOrders(response.data.history || []);
      } catch (err) {
        const errorMessage = err.response?.data?.message || 'Failed to load orders';
        setError(errorMessage);
        toast.error(errorMessage);
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();

    // Subscribe to SSE updates
    const eventSource = subscribeToOrderUpdates(
      (notification) => {
        // Update active orders with new status
        setActiveOrders((prevOrders) =>
          prevOrders.map((order) =>
            order.id === notification.orderId
              ? { ...order, status: notification.status }
              : order
          )
        );
        toast.info(notification.message || `Order ${notification.orderId} updated`);
      },
      (error) => {
        console.error('SSE error:', error);
      }
    );

    return () => eventSource.close();
  }, []);

  if (loading) {
    return <LoadingSpinner />;
  }

  const OrderCard = ({ order, isActive }) => (
    <div className="card mb-4">
      <div className="flex justify-between items-start mb-4">
        <div>
          <h3 className="text-lg font-semibold text-gray-900">
            Order #{order.queueNumber}
          </h3>
          <p className="text-sm text-gray-500">ID: {order.id}</p>
        </div>
        <StatusBadge status={order.status} />
      </div>

      <div className="grid md:grid-cols-2 gap-4 mb-4">
        <div>
          <p className="text-sm text-gray-600">Pickup Time</p>
          <p className="font-semibold flex items-center gap-2">
            <Clock size={16} /> {formatDateTime(order.pickupSlot)}
          </p>
          {isActive && (
            <p className="text-xs text-blue-600 mt-1">
              {getTimeRemaining(order.pickupSlot)}
            </p>
          )}
        </div>
        <div>
          <p className="text-sm text-gray-600">Total Amount</p>
          <p className="text-xl font-bold text-blue-600">
            {formatCurrency(order.totalPrice)}
          </p>
        </div>
      </div>

      <div className="border-t pt-4">
        <h4 className="font-semibold text-gray-900 mb-2">Items</h4>
        <div className="space-y-1 text-sm">
          {order.items?.map((item, idx) => (
            <div key={idx} className="flex justify-between text-gray-700">
              <span>
                {item.itemName} x {item.quantity}
              </span>
              <span>{formatCurrency(item.subtotal)}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );

  return (
    <div className="container-custom py-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">My Orders</h1>

      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg flex items-start gap-3">
          <AlertCircle className="w-5 h-5 text-red-600 mt-0.5 flex-shrink-0" />
          <p className="text-red-700">{error}</p>
        </div>
      )}

      {/* Active Orders */}
      <div className="mb-8">
        <h2 className="text-2xl font-semibold text-gray-900 mb-4">
          Active Orders
        </h2>
        {activeOrders.length === 0 ? (
          <div className="card text-center py-8 text-gray-500">
            <Loader2 size={32} className="mx-auto mb-2 opacity-50" />
            No active orders
          </div>
        ) : (
          activeOrders.map((order) => (
            <OrderCard key={order.id} order={order} isActive={true} />
          ))
        )}
      </div>

      {/* Past Orders */}
      <div>
        <h2 className="text-2xl font-semibold text-gray-900 mb-4">
          Order History
        </h2>
        {pastOrders.length === 0 ? (
          <div className="card text-center py-8 text-gray-500">
            No past orders
          </div>
        ) : (
          pastOrders.map((order) => (
            <OrderCard key={order.id} order={order} isActive={false} />
          ))
        )}
      </div>
    </div>
  );
};

export default OrderHistoryPage;
