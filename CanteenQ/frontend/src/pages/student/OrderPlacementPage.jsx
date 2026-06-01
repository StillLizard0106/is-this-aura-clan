import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ShoppingCart, Plus, Minus, AlertCircle, Clock3, MapPin, BadgeInfo } from 'lucide-react';
import { getMenuItems, getStalls, placeOrder } from '../../api/endpoints';
import { formatCurrency } from '../../utils/currencyFormatter';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { toast } from 'react-toastify';

const OrderPlacementPage = () => {
  const { stallId } = useParams();
  const navigate = useNavigate();
  const [menuItems, setMenuItems] = useState([]);
  const [cart, setCart] = useState([]);
  const [pickupSlot, setPickupSlot] = useState('');
  const [stallInfo, setStallInfo] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [menuResult, stallsResult] = await Promise.allSettled([
          getMenuItems(stallId),
          getStalls(),
        ]);

        if (menuResult.status === 'fulfilled') {
          setMenuItems(menuResult.value.data);
        } else {
          throw menuResult.reason;
        }

        if (stallsResult.status === 'fulfilled') {
          const matchingStall = (stallsResult.value.data ?? []).find((stall) => stall.id === stallId);
          setStallInfo(matchingStall || null);
        }
      } catch (err) {
        const errorMessage = err.response?.data?.message || 'Failed to load menu items';
        setError(errorMessage);
        toast.error(errorMessage);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [stallId]);

  const addToCart = (item) => {
    const existingItem = cart.find((c) => c.menuItemId === item.id);
    if (existingItem) {
      setCart(
        cart.map((c) =>
          c.menuItemId === item.id
            ? { ...c, quantity: c.quantity + 1 }
            : c
        )
      );
    } else {
      setCart([
        ...cart,
        {
          menuItemId: item.id,
          itemName: item.itemName,
          price: item.price,
          quantity: 1,
        },
      ]);
    }
  };

  const removeFromCart = (itemId) => {
    setCart(cart.filter((c) => c.menuItemId !== itemId));
  };

  const updateQuantity = (itemId, quantity) => {
    if (quantity <= 0) {
      removeFromCart(itemId);
    } else {
      setCart(
        cart.map((c) =>
          c.menuItemId === itemId ? { ...c, quantity } : c
        )
      );
    }
  };

  const getTotalPrice = () => {
    return cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
  };

  const formatLocalDatetimeValue = (date) => {
    const pad = (value) => String(value).padStart(2, '0');
    return [
      date.getFullYear(),
      pad(date.getMonth() + 1),
      pad(date.getDate()),
    ].join('-') + `T${pad(date.getHours())}:${pad(date.getMinutes())}`;
  };

  const parseLocalDatetimeValue = (value) => {
    const [datePart, timePart] = value.split('T');
    const [year, month, day] = datePart.split('-').map(Number);
    const [hours, minutes] = timePart.split(':').map(Number);
    return new Date(year, month - 1, day, hours, minutes, 0, 0);
  };

  const isPickupWithinBusinessHours = (value) => {
    const selected = parseLocalDatetimeValue(value);
    const hours = selected.getHours();
    const minutes = selected.getMinutes();
    const timeInMinutes = hours * 60 + minutes;
    return timeInMinutes >= 7 * 60 && timeInMinutes <= 18 * 60;
  };

  const handlePlaceOrder = async () => {
    if (cart.length === 0) {
      toast.error('Please add items to your cart');
      return;
    }

    if (!pickupSlot) {
      toast.error('Please select a pickup time');
      return;
    }

    const selectedPickup = parseLocalDatetimeValue(pickupSlot);
    const now = new Date();
    const minPickup = new Date(now.getTime() + 15 * 60000);
    const maxPickup = new Date(now.getTime() + 7 * 24 * 60 * 60000);

    if (selectedPickup < minPickup) {
      toast.error('Pickup time must be at least 15 minutes from now');
      return;
    }

    if (selectedPickup > maxPickup) {
      toast.error('Pickup time must be within the next 7 days');
      return;
    }

    if (!isPickupWithinBusinessHours(pickupSlot)) {
      toast.error('Pickup time must be between 7:00 AM and 6:00 PM');
      return;
    }

    setSubmitting(true);
    try {
      const orderData = {
        stallId: stallId,
        pickupSlot,
        items: cart.map((item) => ({
          menuItemId: item.menuItemId,
          quantity: item.quantity,
        })),
      };

      const response = await placeOrder(orderData);
      toast.success('Order placed successfully!');
      navigate(`/order/${response.data.id}/confirmation`);
    } catch (err) {
      const errorMessage = err.response?.data?.message || 'Failed to place order';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return <LoadingSpinner />;
  }

  const now = new Date();
  const minPickupTime = formatLocalDatetimeValue(
    new Date(now.getTime() + 15 * 60000)
  );
  const maxPickupTime = formatLocalDatetimeValue(
    new Date(now.getTime() + 7 * 24 * 60 * 60000)
  );

  return (
    <div
      className="min-h-screen py-8"
      style={{
        background:
          'radial-gradient(circle at top left, rgba(37, 99, 235, 0.08), transparent 28%), radial-gradient(circle at top right, rgba(245, 168, 0, 0.08), transparent 24%), linear-gradient(180deg, #f8fbff 0%, #f3f7ff 100%)',
      }}
    >
      <div className="container-custom">
        <div className="mb-8 rounded-[28px] border border-white/70 bg-white/80 p-6 shadow-[0_18px_50px_rgba(15,23,42,0.08)] backdrop-blur-xl">
          <div className="flex flex-col gap-6 lg:flex-row lg:items-end lg:justify-between">
            <div className="max-w-2xl">
              <p className="mb-3 inline-flex items-center gap-2 rounded-full bg-blue-50 px-3 py-1 text-xs font-semibold uppercase tracking-[0.28em] text-blue-700">
                <BadgeInfo size={14} />
                Browse Menu
              </p>
              <h1 className="mb-3 text-4xl font-bold tracking-tight text-slate-950 md:text-5xl">
                Place your order
              </h1>
              <p className="max-w-xl text-sm leading-6 text-slate-600 md:text-base">
                Pick items from the stall below, build your cart, and choose a pickup time that fits your schedule.
              </p>
            </div>

            {stallInfo && (
              <div className="grid gap-3 rounded-[24px] border border-blue-100 bg-blue-950 px-5 py-4 text-white shadow-[0_14px_36px_rgba(15,23,42,0.18)]">
                <div>
                  <p className="text-[11px] font-semibold uppercase tracking-[0.22em] text-blue-200">Current Stall</p>
                  <h2 className="mt-1 text-2xl font-semibold text-white">{stallInfo.stallName}</h2>
                  <p className="mt-1 text-sm text-blue-100">{stallInfo.vendorName}</p>
                </div>
                <div className="grid gap-2 text-sm text-blue-50 sm:grid-cols-2">
                  <div className="rounded-2xl bg-white/10 px-3 py-2">
                    <span className="flex items-center gap-2 text-blue-100">
                      <Clock3 size={14} />
                      Hours
                    </span>
                    <p className="mt-1 font-semibold text-white">{stallInfo.operatingHours || 'Not specified'}</p>
                  </div>
                  <div className="rounded-2xl bg-white/10 px-3 py-2">
                    <span className="flex items-center gap-2 text-blue-100">
                      <MapPin size={14} />
                      Queue
                    </span>
                    <p className="mt-1 font-semibold text-white">
                      {Math.max((stallInfo.queueLimit ?? 100) - (stallInfo.queueSlotsLeft ?? 100), 0)}/{stallInfo.queueLimit ?? 100}
                    </p>
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>

      {error && (
        <div className="mb-6 flex items-start gap-3 rounded-2xl border border-red-200 bg-red-50 p-4 shadow-sm">
          <AlertCircle className="w-5 h-5 text-red-600 mt-0.5 flex-shrink-0" />
          <p className="text-red-700">{error}</p>
        </div>
      )}

      <div className="grid gap-6 lg:grid-cols-[minmax(0,1.7fr)_minmax(340px,1fr)]">
        <div className="rounded-[28px] border border-white/70 bg-white/85 p-5 shadow-[0_18px_50px_rgba(15,23,42,0.08)] backdrop-blur-xl md:p-6">
          <div className="mb-5 flex items-center justify-between">
            <div>
              <h2 className="text-2xl font-semibold tracking-tight text-slate-950">Menu Items</h2>
              <p className="text-sm text-slate-500">Tap an item to add it to your cart.</p>
            </div>
            <div className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold uppercase tracking-[0.22em] text-slate-500">
              {menuItems.length} items
            </div>
          </div>

          <div className="grid gap-4">
            {menuItems.map((item) => (
              <div
                key={item.id}
                className="group rounded-2xl border border-slate-200 bg-white p-4 shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:border-blue-200 hover:shadow-[0_18px_30px_rgba(37,99,235,0.08)]"
              >
                <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                  <div className="min-w-0 flex-1">
                    <div className="mb-2 flex items-center gap-3">
                      <h3 className="truncate text-lg font-semibold text-slate-950">
                        {item.itemName}
                      </h3>
                      <span className="rounded-full bg-blue-50 px-2.5 py-1 text-xs font-semibold text-blue-700">
                        {formatCurrency(item.price)}
                      </span>
                    </div>
                    <p className="text-sm leading-6 text-slate-600">
                      {item.description || 'No description provided.'}
                    </p>
                  </div>

                  <button
                    onClick={() => addToCart(item)}
                    disabled={item.available === false}
                    className="inline-flex items-center justify-center gap-2 rounded-xl bg-blue-600 px-4 py-3 text-sm font-semibold text-white shadow-[0_12px_24px_rgba(37,99,235,0.22)] transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:bg-slate-300"
                  >
                    <Plus size={18} />
                    Add
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>

        <div>
          <div className="sticky top-4 rounded-[28px] border border-slate-200 bg-slate-950 p-5 text-white shadow-[0_24px_60px_rgba(15,23,42,0.22)] md:p-6">
            <h2 className="mb-4 flex items-center gap-2 text-xl font-semibold">
              <ShoppingCart size={20} /> Order Summary
            </h2>

            <div className="mb-4 rounded-2xl border border-white/10 bg-white/5 p-4">
              <div className="flex items-center justify-between text-sm font-semibold text-slate-100">
                <span>Queue</span>
                <span>
                  {stallInfo ? Math.max((stallInfo.queueLimit ?? 100) - (stallInfo.queueSlotsLeft ?? 100), 0) : 0}
                  /{stallInfo?.queueLimit ?? 100}
                </span>
              </div>
              <p className="mt-1 text-xs text-slate-300">
                {stallInfo ? `${stallInfo.queueSlotsLeft ?? 100} slots left` : 'Queue info unavailable'}
              </p>
            </div>

            <div className="mb-4 max-h-72 space-y-3 overflow-y-auto pr-1">
              {cart.length === 0 ? (
                <div className="rounded-2xl border border-dashed border-white/15 bg-white/5 p-5 text-center text-sm text-slate-300">
                  No items in cart yet.
                </div>
              ) : (
                cart.map((item) => (
                  <div
                    key={item.menuItemId}
                    className="flex items-center justify-between gap-3 rounded-2xl border border-white/10 bg-white/5 p-3"
                  >
                    <div className="min-w-0 flex-1">
                      <p className="truncate text-sm font-semibold text-white">{item.itemName}</p>
                      <p className="text-xs text-slate-300">
                        {formatCurrency(item.price)} x {item.quantity}
                      </p>
                    </div>
                    <div className="flex items-center gap-1 rounded-xl bg-black/20 p-1">
                      <button
                        onClick={() => updateQuantity(item.menuItemId, item.quantity - 1)}
                        className="rounded-lg p-2 text-white/80 transition hover:bg-white/10 hover:text-white"
                      >
                        <Minus size={16} />
                      </button>
                      <span className="w-7 text-center text-sm font-semibold">{item.quantity}</span>
                      <button
                        onClick={() => updateQuantity(item.menuItemId, item.quantity + 1)}
                        className="rounded-lg p-2 text-white/80 transition hover:bg-white/10 hover:text-white"
                      >
                        <Plus size={16} />
                      </button>
                    </div>
                  </div>
                ))
              )}
            </div>

            <div className="mb-4 border-t border-white/10 pt-4">
              <div className="mb-4 flex items-center justify-between">
                <span className="font-semibold text-slate-200">Total</span>
                <span className="text-3xl font-bold tracking-tight text-white">
                  {formatCurrency(getTotalPrice())}
                </span>
              </div>

              <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
                <label className="mb-2 block text-sm font-semibold text-slate-100">Pickup Time</label>
                <input
                  type="datetime-local"
                  className="w-full rounded-xl border border-white/10 bg-slate-900/70 px-4 py-3 text-sm text-white outline-none ring-0 transition placeholder:text-slate-400 focus:border-blue-400"
                  value={pickupSlot}
                  onChange={(e) => setPickupSlot(e.target.value)}
                  min={minPickupTime}
                  max={maxPickupTime}
                />
                <p className="mt-2 text-xs leading-5 text-slate-300">
                  Choose a pickup time 15 minutes to 7 days from now, between 7:00 AM and 6:00 PM.
                </p>
              </div>
            </div>

            <button
              onClick={handlePlaceOrder}
              disabled={submitting}
              className="w-full rounded-xl bg-gradient-to-r from-amber-400 to-yellow-300 px-4 py-3 text-sm font-bold text-slate-950 shadow-[0_16px_30px_rgba(245,168,0,0.28)] transition hover:brightness-105 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {submitting ? 'Placing order...' : 'Place Order'}
            </button>
          </div>
        </div>
      </div>
      </div>
    </div>
  );
};

export default OrderPlacementPage;
