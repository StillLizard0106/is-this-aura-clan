import React, { useState, useEffect, useCallback } from 'react';
import { AlertCircle, RefreshCw, Filter, BadgeInfo, Clock3 } from 'lucide-react';
import { getMyStalls, getStaffOrders, updateOrderStatus } from '../../api/endpoints';
import StatusBadge from '../../components/shared/StatusBadge';
import { formatDateTime, getTimeRemaining } from '../../utils/dateFormatter';
import { formatCurrency } from '../../utils/currencyFormatter';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { toast } from 'react-toastify';

const ORDER_STATUS_OPTIONS = {
  PENDING: ['PREPARING'],
  PREPARING: ['READY'],
  READY: ['COMPLETED'],
  COMPLETED: [],
  CANCELLED: [],
  UNCLAIMED: [],
};

const StaffQueuePage = () => {
  const [orders, setOrders] = useState([]);
  const [assignedStalls, setAssignedStalls] = useState([]);
  const [selectedStallId, setSelectedStallId] = useState('');
  const [loadingStalls, setLoadingStalls] = useState(true);
  const [loadingOrders, setLoadingOrders] = useState(false);
  const [error, setError] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  const [updatingId, setUpdatingId] = useState(null);

  useEffect(() => {
    let isActive = true;

    const fetchAssignedStalls = async () => {
      try {
        setLoadingStalls(true);
        setError('');
        const response = await getMyStalls();
        if (!isActive) {
          return;
        }

        const stalls = response.data ?? [];
        setAssignedStalls(stalls);

        if (stalls.length > 0) {
          setSelectedStallId((current) => current || String(stalls[0].stallId));
        } else {
          setOrders([]);
          setError('No stall assignment found for your account.');
        }
      } catch (err) {
        if (!isActive) {
          return;
        }

        const errorMessage = err.response?.data?.message || 'Failed to load assigned stalls';
        setAssignedStalls([]);
        setOrders([]);
        setError(errorMessage);
        toast.error(errorMessage);
      } finally {
        if (isActive) {
          setLoadingStalls(false);
        }
      }
    };

    fetchAssignedStalls();

    return () => {
      isActive = false;
    };
  }, []);

  const fetchOrders = useCallback(async (stallId) => {
    if (!stallId) {
      return;
    }

    try {
      setLoadingOrders(true);
      setError('');
      const params = { stallId };
      if (statusFilter) {
        params.status = statusFilter;
      }

      const response = await getStaffOrders(params);
      const queue = response.data;
      setOrders(Array.isArray(queue) ? queue : queue?.orders ?? []);
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'Failed to load orders';
      setOrders([]);
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setLoadingOrders(false);
    }
  }, [statusFilter]);

  useEffect(() => {
    if (!selectedStallId) {
      return;
    }

    fetchOrders(selectedStallId);
  }, [selectedStallId, fetchOrders]);

  const handleStatusUpdate = async (orderId, currentStatus, newStatus) => {
    setUpdatingId(orderId);
    try {
      await updateOrderStatus(orderId, newStatus);
      setOrders((prevOrders) =>
        newStatus === 'CANCELLED'
          ? prevOrders.filter((order) => order.id !== orderId)
          : prevOrders.map((order) =>
              order.id === orderId ? { ...order, status: newStatus } : order
            )
      );
      toast.success(
        newStatus === 'CANCELLED'
          ? 'Order rejected'
          : `Order status updated to ${newStatus}`
      );
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'Failed to update order status';
      toast.error(errorMessage);
    } finally {
      setUpdatingId(null);
    }
  };

  const handleRejectOrder = async (orderId) => {
    await handleStatusUpdate(orderId, 'PENDING', 'CANCELLED');
  };

  if (loadingStalls || loadingOrders) {
    return <LoadingSpinner />;
  }

  const statusOptions = ['', 'PENDING', 'PREPARING', 'READY', 'COMPLETED', 'UNCLAIMED'];

  return (
    <div
      className="min-h-screen py-8"
      style={{
        background:
          'radial-gradient(circle at top left, rgba(37,99,235,0.12), transparent 22%), radial-gradient(circle at top right, rgba(245,168,0,0.08), transparent 22%), linear-gradient(180deg, #f7fbff 0%, #eef4fb 100%)',
      }}
    >
      <div className="container-custom">
        <div className="mb-6 flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div>
            <p className="mb-2 inline-flex items-center gap-2 rounded-full bg-blue-50 px-3 py-1 text-xs font-semibold uppercase tracking-[0.28em] text-blue-700">
              <BadgeInfo size={14} />
              Queue Control
            </p>
            <h1 className="text-4xl font-bold tracking-tight text-slate-950">Order Queue</h1>
          </div>
          <button
            onClick={() => fetchOrders(selectedStallId)}
            className="inline-flex items-center gap-2 rounded-xl bg-slate-950 px-4 py-3 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-50"
            disabled={!selectedStallId}
          >
            <RefreshCw size={18} /> Refresh
          </button>
        </div>

        {error && (
          <div className="mb-6 flex items-start gap-3 rounded-2xl border border-red-200 bg-red-50 p-4 shadow-sm">
            <AlertCircle className="mt-0.5 h-5 w-5 flex-shrink-0 text-red-600" />
            <p className="text-red-700">{error}</p>
          </div>
        )}

        <div className="mb-6 rounded-[28px] border border-white/70 bg-white/85 p-5 shadow-[0_18px_50px_rgba(15,23,42,0.08)] backdrop-blur-xl">
          <div className="grid gap-4 lg:grid-cols-2 lg:items-center">
            <div className="flex items-center gap-3">
              <label className="font-semibold text-slate-700">Active Stall:</label>
              <select
                value={selectedStallId}
                onChange={(e) => setSelectedStallId(e.target.value)}
                className="min-w-[240px] rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm outline-none"
              >
                {assignedStalls.map((stall) => (
                  <option key={stall.stallId} value={stall.stallId}>
                    {stall.stallName} - {stall.vendorName}
                  </option>
                ))}
              </select>
            </div>

            <div className="flex items-center gap-3">
              <Filter size={20} />
              <label className="font-semibold text-slate-700">Filter by Status:</label>
              <select
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
                className="rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm outline-none"
              >
                {statusOptions.map((status) => (
                  <option key={status || 'all'} value={status}>
                    {status || 'All Orders'}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>

        {orders.length === 0 ? (
          <div className="rounded-[28px] border border-dashed border-slate-300 bg-white/70 py-12 text-center text-slate-500 shadow-sm">
            No orders found
          </div>
        ) : (
          <div className="overflow-hidden rounded-[28px] border border-white/70 bg-white/90 shadow-[0_18px_50px_rgba(15,23,42,0.08)] backdrop-blur-xl">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="border-b border-slate-200 bg-slate-950 text-white">
                  <tr>
                    <th className="px-4 py-4 text-left text-sm font-semibold">Queue #</th>
                    <th className="px-4 py-4 text-left text-sm font-semibold">Student</th>
                    <th className="px-4 py-4 text-left text-sm font-semibold">Pickup Time</th>
                    <th className="px-4 py-4 text-left text-sm font-semibold">Total</th>
                    <th className="px-4 py-4 text-left text-sm font-semibold">Status</th>
                    <th className="px-4 py-4 text-left text-sm font-semibold">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {orders.map((order) => (
                    <tr key={order.id} className="border-b border-slate-100 hover:bg-slate-50/80">
                      <td className="px-4 py-4 font-bold text-lg text-blue-600">#{order.queueNumber}</td>
                      <td className="px-4 py-4">
                        <div>
                          <p className="font-semibold text-slate-950">{order.studentName}</p>
                          <p className="text-sm text-slate-500">{order.email}</p>
                        </div>
                      </td>
                      <td className="px-4 py-4">
                        <p className="text-sm text-slate-800">{formatDateTime(order.pickupSlot)}</p>
                        <p className="text-xs text-slate-500">{getTimeRemaining(order.pickupSlot)}</p>
                      </td>
                      <td className="px-4 py-4 font-semibold text-slate-900">{formatCurrency(order.totalPrice)}</td>
                      <td className="px-4 py-4">
                        <StatusBadge status={order.status} />
                      </td>
                      <td className="px-4 py-4">
                        <div className="flex flex-col gap-2">
                          <select
                            value=""
                            onChange={(e) => {
                              if (e.target.value) {
                                handleStatusUpdate(order.id, order.status, e.target.value);
                              }
                            }}
                            disabled={updatingId === order.id}
                            className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm outline-none"
                          >
                            <option value="">Update Status</option>
                            {ORDER_STATUS_OPTIONS[order.status]?.map((status) => (
                              <option key={status} value={status}>
                                {status}
                              </option>
                            ))}
                          </select>

                          {order.status === 'PENDING' && (
                            <button
                              type="button"
                              onClick={() => handleRejectOrder(order.id)}
                              disabled={updatingId === order.id}
                              className="rounded-xl bg-red-600 px-3 py-2 text-sm font-semibold text-white transition hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-50"
                            >
                              Reject
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default StaffQueuePage;
