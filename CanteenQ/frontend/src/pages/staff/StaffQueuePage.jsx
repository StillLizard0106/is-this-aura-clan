import React, { useState, useEffect, useCallback } from 'react';
import { AlertCircle, RefreshCw, Filter } from 'lucide-react';
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
    <div className="container-custom py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Order Queue</h1>
        <button
          onClick={() => fetchOrders(selectedStallId)}
          className="btn-secondary flex items-center gap-2"
          disabled={!selectedStallId}
        >
          <RefreshCw size={18} /> Refresh
        </button>
      </div>

      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg flex items-start gap-3">
          <AlertCircle className="w-5 h-5 text-red-600 mt-0.5 flex-shrink-0" />
          <p className="text-red-700">{error}</p>
        </div>
      )}

      <div className="card mb-6">
        <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div className="flex items-center gap-3">
            <label className="font-semibold text-gray-700">Active Stall:</label>
            <select
              value={selectedStallId}
              onChange={(e) => setSelectedStallId(e.target.value)}
              className="input-field min-w-[240px]"
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
            <label className="font-semibold text-gray-700">Filter by Status:</label>
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="input-field"
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
        <div className="card text-center py-12">
          <p className="text-gray-500 text-lg">No orders found</p>
        </div>
      ) : (
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-100 border-b-2 border-gray-300">
              <tr>
                <th className="px-4 py-2 text-left">Queue #</th>
                <th className="px-4 py-2 text-left">Student</th>
                <th className="px-4 py-2 text-left">Pickup Time</th>
                <th className="px-4 py-2 text-left">Total</th>
                <th className="px-4 py-2 text-left">Status</th>
                <th className="px-4 py-2 text-left">Action</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => (
                <tr key={order.id} className="border-b hover:bg-gray-50">
                  <td className="px-4 py-3 font-bold text-lg text-blue-600">
                    #{order.queueNumber}
                  </td>
                  <td className="px-4 py-3">
                    <div>
                      <p className="font-semibold text-gray-900">
                        {order.studentName}
                      </p>
                      <p className="text-sm text-gray-600">{order.email}</p>
                    </div>
                  </td>
                  <td className="px-4 py-3">
                    <p className="text-sm">{formatDateTime(order.pickupSlot)}</p>
                    <p className="text-xs text-gray-500">
                      {getTimeRemaining(order.pickupSlot)}
                    </p>
                  </td>
                  <td className="px-4 py-3 font-semibold">
                    {formatCurrency(order.totalPrice)}
                  </td>
                  <td className="px-4 py-3">
                    <StatusBadge status={order.status} />
                  </td>
                  <td className="px-4 py-3">
                    <div className="flex flex-col gap-2">
                      <select
                        value=""
                        onChange={(e) => {
                          if (e.target.value) {
                            handleStatusUpdate(
                              order.id,
                              order.status,
                              e.target.value
                            );
                          }
                        }}
                        disabled={updatingId === order.id}
                        className="input-field text-sm"
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
                          className="rounded-lg bg-red-600 px-3 py-2 text-sm font-semibold text-white hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-50"
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
      )}
    </div>
  );
};

export default StaffQueuePage;
