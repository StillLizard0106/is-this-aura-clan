import React, { useState, useEffect } from 'react';
import { Clock, AlertCircle, Loader2, History, Sparkles } from 'lucide-react';
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

    const eventSource = subscribeToOrderUpdates(
      (notification) => {
        setActiveOrders((prevOrders) =>
          prevOrders.map((order) =>
            order.id === notification.orderId
              ? { ...order, status: notification.status }
              : order
          )
        );
        toast.info(notification.message || `Order ${notification.orderId} updated`);
      },
      (sseError) => {
        console.error('SSE error:', sseError);
      }
    );

    return () => eventSource.close();
  }, []);

  if (loading) {
    return <LoadingSpinner />;
  }

  const OrderCard = ({ order, isActive }) => (
    <div className="rounded-[24px] border border-white/70 bg-white/85 p-5 shadow-[0_14px_40px_rgba(15,23,42,0.08)] backdrop-blur-xl">
      <div className="mb-4 flex items-start justify-between gap-4">
        <div>
          <div className="mb-2 inline-flex items-center gap-2 rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold uppercase tracking-[0.2em] text-slate-500">
            <History size={14} />
            {isActive ? 'Active' : 'Past'}
          </div>
          <h3 className="text-lg font-semibold text-slate-950">Order #{order.queueNumber}</h3>
          <p className="text-sm text-slate-500">ID: {order.id}</p>
        </div>
        <StatusBadge status={order.status} />
      </div>

      <div className="mb-4 grid gap-4 md:grid-cols-2">
        <div className="rounded-2xl bg-blue-50 p-4">
          <p className="text-sm text-slate-600">Pickup Time</p>
          <p className="mt-1 flex items-center gap-2 font-semibold text-slate-950">
            <Clock size={16} /> {formatDateTime(order.pickupSlot)}
          </p>
          {isActive && (
            <p className="mt-1 text-xs font-semibold text-blue-700">
              {getTimeRemaining(order.pickupSlot)}
            </p>
          )}
        </div>
        <div className="rounded-2xl bg-emerald-50 p-4">
          <p className="text-sm text-slate-600">Total Amount</p>
          <p className="mt-1 text-2xl font-bold text-emerald-700">
            {formatCurrency(order.totalPrice)}
          </p>
        </div>
      </div>

      <div className="border-t border-slate-200 pt-4">
        <h4 className="mb-2 font-semibold text-slate-950">Items</h4>
        <div className="space-y-2 text-sm">
          {order.items?.map((item, idx) => (
            <div key={idx} className="flex justify-between rounded-xl bg-slate-50 px-3 py-2 text-slate-700">
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
    <div
      className="min-h-screen py-8"
      style={{
        background:
          'radial-gradient(circle at top left, rgba(245,158,11,0.10), transparent 22%), radial-gradient(circle at top right, rgba(14,165,233,0.10), transparent 25%), linear-gradient(180deg, #fffdf8 0%, #f4f8ff 100%)',
      }}
    >
      <div className="container-custom">
        <div className="mb-8 rounded-[28px] border border-white/70 bg-white/80 p-6 shadow-[0_18px_50px_rgba(15,23,42,0.08)] backdrop-blur-xl">
          <p className="mb-3 inline-flex items-center gap-2 rounded-full bg-amber-50 px-3 py-1 text-xs font-semibold uppercase tracking-[0.28em] text-amber-700">
            <Sparkles size={14} />
            Order Timeline
          </p>
          <h1 className="text-4xl font-bold tracking-tight text-slate-950 md:text-5xl">My orders</h1>
          <p className="mt-3 max-w-2xl text-sm leading-6 text-slate-600 md:text-base">
            Track active orders, review pickup times, and look back at your recent canteen history.
          </p>
        </div>

        {error && (
          <div className="mb-6 flex items-start gap-3 rounded-2xl border border-red-200 bg-red-50 p-4 shadow-sm">
            <AlertCircle className="mt-0.5 h-5 w-5 flex-shrink-0 text-red-600" />
            <p className="text-red-700">{error}</p>
          </div>
        )}

        <div className="mb-8">
          <h2 className="mb-4 text-2xl font-semibold text-slate-950">Active orders</h2>
          {activeOrders.length === 0 ? (
            <div className="rounded-[24px] border border-dashed border-slate-300 bg-white/70 py-10 text-center text-slate-500">
              <Loader2 size={32} className="mx-auto mb-2 opacity-50" />
              No active orders
            </div>
          ) : (
            <div className="grid gap-4">
              {activeOrders.map((order) => (
                <OrderCard key={order.id} order={order} isActive={true} />
              ))}
            </div>
          )}
        </div>

        <div>
          <h2 className="mb-4 text-2xl font-semibold text-slate-950">Order history</h2>
          {pastOrders.length === 0 ? (
            <div className="rounded-[24px] border border-dashed border-slate-300 bg-white/70 py-10 text-center text-slate-500">
              No past orders
            </div>
          ) : (
            <div className="grid gap-4">
              {pastOrders.map((order) => (
                <OrderCard key={order.id} order={order} isActive={false} />
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default OrderHistoryPage;
