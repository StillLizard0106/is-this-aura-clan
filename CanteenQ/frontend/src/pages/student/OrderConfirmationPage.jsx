import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { CheckCircle, Clock, ArrowLeft, Sparkles, ReceiptText } from 'lucide-react';
import { getOrderDetail } from '../../api/endpoints';
import { formatDateTime } from '../../utils/dateFormatter';
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
      <div
        className="min-h-screen py-12"
        style={{
          background:
            'radial-gradient(circle at top left, rgba(239,68,68,0.10), transparent 22%), radial-gradient(circle at top right, rgba(37,99,235,0.10), transparent 25%), linear-gradient(180deg, #fff8f8 0%, #f2f7ff 100%)',
        }}
      >
        <div className="container-custom flex min-h-screen items-center justify-center">
          <div className="w-full max-w-lg rounded-[32px] border border-white/70 bg-white/85 p-8 text-center shadow-[0_22px_60px_rgba(15,23,42,0.12)] backdrop-blur-xl">
            <AlertCircle className="mx-auto mb-4 h-16 w-16 text-red-500" />
            <h1 className="mb-2 text-3xl font-bold tracking-tight text-slate-950">Order not found</h1>
            <p className="mb-6 text-slate-600">{error || 'Unable to load the order confirmation.'}</p>
            <button
              onClick={() => navigate('/orders')}
              className="w-full rounded-xl bg-blue-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-blue-700"
            >
              View Orders
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div
      className="min-h-screen py-12"
      style={{
        background:
          'radial-gradient(circle at top left, rgba(34,197,94,0.12), transparent 20%), radial-gradient(circle at top right, rgba(245,168,0,0.10), transparent 24%), linear-gradient(180deg, #f7fff9 0%, #f4f8ff 100%)',
      }}
    >
      <div className="container-custom flex min-h-screen items-center justify-center">
        <div className="w-full max-w-3xl overflow-hidden rounded-[32px] border border-white/70 bg-white/85 shadow-[0_24px_70px_rgba(15,23,42,0.12)] backdrop-blur-xl">
          <div className="grid lg:grid-cols-[0.95fr_1.05fr]">
            <div className="bg-slate-950 p-8 text-white md:p-10">
              <p className="mb-4 inline-flex items-center gap-2 rounded-full bg-white/10 px-3 py-1 text-xs font-semibold uppercase tracking-[0.28em] text-emerald-200">
                <Sparkles size={14} />
                Pickup Confirmed
              </p>
              <CheckCircle className="mb-4 h-16 w-16 text-emerald-400" />
              <h1 className="mb-3 text-4xl font-bold tracking-tight">Order placed!</h1>
              <p className="max-w-md text-sm leading-6 text-slate-300">
                Your order is in the queue. Keep this screen open or head back to your order list.
              </p>

              <div className="mt-8 rounded-[24px] border border-white/10 bg-white/5 p-4">
                <div className="mb-2 flex items-center gap-2 text-sm font-semibold text-slate-100">
                  <ReceiptText size={16} />
                  Pickup summary
                </div>
                <p className="text-xs leading-5 text-slate-300">
                  Queue number: {order.queueNumber}
                  <br />
                  Pickup window: {formatDateTime(order.pickupSlot)}
                </p>
              </div>

              <button
                onClick={() => navigate('/dashboard')}
                className="mt-8 inline-flex items-center gap-2 rounded-xl border border-white/15 bg-white/10 px-4 py-3 text-sm font-semibold text-white transition hover:bg-white/15"
              >
                <ArrowLeft size={16} />
                Back to stalls
              </button>
            </div>

            <div className="p-8 md:p-10">
              <div className="mb-6 rounded-[24px] bg-blue-50 p-5 text-left">
                <div className="mb-4">
                  <p className="text-sm text-slate-600">Order ID</p>
                  <p className="break-all font-mono text-lg font-semibold text-slate-950">{order.id}</p>
                </div>
                <div className="mb-4">
                  <p className="text-sm text-slate-600">Queue Number</p>
                  <p className="text-5xl font-bold tracking-tight text-blue-600">{order.queueNumber}</p>
                </div>
                <div className="mb-4">
                  <p className="flex items-center gap-1 text-sm text-slate-600">
                    <Clock size={16} /> Pickup Time
                  </p>
                  <p className="font-semibold text-slate-950">{formatDateTime(order.pickupSlot)}</p>
                </div>
                <div>
                  <p className="text-sm text-slate-600">Total Amount</p>
                  <p className="text-2xl font-bold text-blue-600">{formatCurrency(order.totalPrice)}</p>
                </div>
              </div>

              <div className="mb-6 rounded-[24px] border border-amber-200 bg-amber-50 p-5 text-left text-sm">
                <p className="mb-2 font-semibold text-amber-900">Important</p>
                <ul className="space-y-2 text-xs leading-5 text-amber-800">
                  <li>Pick up your order by <strong>{formatDateTime(order.pickupSlot)}</strong></li>
                  <li>Your queue number is <strong>{order.queueNumber}</strong></li>
                  <li>You will receive notifications when your order is ready</li>
                  <li>Unclaimed orders expire 10 minutes after pickup time</li>
                </ul>
              </div>

              <button
                onClick={() => navigate('/orders')}
                className="mb-3 w-full rounded-xl bg-blue-600 px-4 py-3 text-sm font-semibold text-white transition hover:bg-blue-700"
              >
                View Order Details
              </button>
              <button
                onClick={() => navigate('/dashboard')}
                className="w-full rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
              >
                Continue Shopping
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default OrderConfirmationPage;
