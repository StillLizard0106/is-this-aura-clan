import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ShoppingCart, Plus, Minus, AlertCircle } from 'lucide-react';
import { getMenuItems, getStalls, placeOrder } from '../../api/endpoints';
import { formatCurrency } from '../../utils/currencyFormatter';
import { formatDateTime } from '../../utils/dateFormatter';
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
    <div className="container-custom py-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-6">Place Your Order</h1>

      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg flex items-start gap-3">
          <AlertCircle className="w-5 h-5 text-red-600 mt-0.5 flex-shrink-0" />
          <p className="text-red-700">{error}</p>
        </div>
      )}

      <div className="grid md:grid-cols-3 gap-6">
        {/* Menu Items */}
        <div className="md:col-span-2">
          <div className="card mb-6">
            <h2 className="text-xl font-semibold mb-4">Menu Items</h2>
            <div className="space-y-3">
              {menuItems.map((item) => (
                <div
                  key={item.id}
                  className="flex justify-between items-center p-4 border rounded-lg hover:bg-gray-50"
                >
                  <div className="flex-1">
                    <h3 className="font-semibold text-gray-900">
                      {item.itemName}
                    </h3>
                    <p className="text-sm text-gray-600">{item.description}</p>
                    <p className="text-lg font-bold text-blue-600 mt-1">
                      {formatCurrency(item.price)}
                    </p>
                  </div>
                  <button
                    onClick={() => addToCart(item)}
                    disabled={item.available === false}
                    className="btn-primary"
                  >
                    <Plus size={18} />
                  </button>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Order Summary */}
        <div>
          <div className="card sticky top-4">
            <h2 className="text-xl font-semibold mb-4 flex items-center gap-2">
              <ShoppingCart size={20} /> Order Summary
            </h2>

            {stallInfo && (
              <div className="mb-4 rounded-lg border border-blue-100 bg-blue-50 p-3">
                <div className="flex items-center justify-between text-sm font-semibold text-blue-900">
                  <span>Queue</span>
                  <span>
                    {Math.max((stallInfo.queueLimit ?? 100) - (stallInfo.queueSlotsLeft ?? 100), 0)}
                    /{stallInfo.queueLimit ?? 100}
                  </span>
                </div>
                <p className="text-xs text-blue-800 mt-1">
                  {stallInfo.queueSlotsLeft ?? 100} slots left
                </p>
              </div>
            )}

            {/* Cart Items */}
            <div className="mb-4 max-h-64 overflow-y-auto">
              {cart.length === 0 ? (
                <p className="text-gray-500 text-sm">No items in cart</p>
              ) : (
                cart.map((item) => (
                  <div
                    key={item.menuItemId}
                    className="flex justify-between items-center mb-3 p-2 border rounded"
                  >
                    <div className="flex-1">
                      <p className="font-semibold text-sm">{item.itemName}</p>
                      <p className="text-xs text-gray-600">
                        {formatCurrency(item.price)} x {item.quantity}
                      </p>
                    </div>
                    <div className="flex items-center gap-1">
                      <button
                        onClick={() =>
                          updateQuantity(
                            item.menuItemId,
                            item.quantity - 1
                          )
                        }
                        className="p-1 hover:bg-gray-200 rounded"
                      >
                        <Minus size={16} />
                      </button>
                      <span className="w-6 text-center">{item.quantity}</span>
                      <button
                        onClick={() =>
                          updateQuantity(
                            item.menuItemId,
                            item.quantity + 1
                          )
                        }
                        className="p-1 hover:bg-gray-200 rounded"
                      >
                        <Plus size={16} />
                      </button>
                    </div>
                  </div>
                ))
              )}
            </div>

            {/* Total */}
            <div className="border-t pt-4 mb-4">
              <div className="flex justify-between items-center mb-4">
                <span className="font-semibold text-gray-900">Total:</span>
                <span className="text-2xl font-bold text-blue-600">
                  {formatCurrency(getTotalPrice())}
                </span>
              </div>

              {/* Pickup Slot */}
              <div className="form-group">
                <label className="form-label">Pickup Time</label>
                <input
                  type="datetime-local"
                  className="input-field"
                  value={pickupSlot}
                  onChange={(e) => setPickupSlot(e.target.value)}
                  min={minPickupTime}
                  max={maxPickupTime}
                />
                <p className="text-xs text-gray-500 mt-1">
                  Must be 15 minutes to 7 days from now and between 7:00 AM and 6:00 PM
                </p>
              </div>
            </div>

            <button
              onClick={handlePlaceOrder}
              disabled={submitting}
              className="btn-primary w-full"
            >
              {submitting ? 'Placing order...' : 'Place Order'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default OrderPlacementPage;
