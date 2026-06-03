import { useEffect, useState } from 'react';
import { Loader2, PlusCircle, Store } from 'lucide-react';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';

import {
  createStaffMenuItem,
  getStaffMenuItems,
  updateStaffMenuItem,
  deleteStaffMenuItem,
  getMyStalls,
} from '../../api/endpoints';
import { buildMenuItemPayload, validateMenuItemForm } from '../../utils/menuItemForm';

const initialFormState = {
  itemName: '',
  description: '',
  price: '',
  category: '',
  available: true,
};

const StaffMenuItemManagementPage = () => {
  const [stalls, setStalls] = useState([]);
  const [selectedStallId, setSelectedStallId] = useState('');
  const [menuItems, setMenuItems] = useState([]);
  const [editingItem, setEditingItem] = useState(null);
  const [loadingStalls, setLoadingStalls] = useState(true);
  const [loadingMenuItems, setLoadingMenuItems] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [form, setForm] = useState(initialFormState);
  const [errors, setErrors] = useState({});
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    let isActive = true;

    const fetchStalls = async () => {
      try {
        setLoadingStalls(true);
        setError('');
        const response = await getMyStalls();
        if (!isActive) {
          return;
        }

        const assignedStalls = response.data ?? [];
        setStalls(assignedStalls);

        if (assignedStalls.length > 0) {
          setSelectedStallId((current) => current || String(assignedStalls[0].stallId));
        } else {
          setError('No stall assignment found for your account.');
          setSuccessMessage('');
        }
      } catch (fetchError) {
        if (!isActive) {
          return;
        }

        const message = fetchError.response?.data?.message || 'Failed to load assigned stalls';
        setStalls([]);
        setError(message);
        toast.error(message);
      } finally {
        if (isActive) {
          setLoadingStalls(false);
        }
      }
    };

    fetchStalls();

    return () => {
      isActive = false;
    };
  }, []);

  useEffect(() => {
    if (!selectedStallId) {
      setMenuItems([]);
      setEditingItem(null);
      return;
    }

    const cancelIfStale = { active: true };
    const loadMenuItems = async () => {
      try {
        setLoadingMenuItems(true);
        setError('');
        const response = await getStaffMenuItems(selectedStallId);
        if (cancelIfStale.active) {
          setMenuItems(response.data ?? []);
        }
      } catch (fetchError) {
        if (cancelIfStale.active) {
          const message = fetchError.response?.data?.message || 'Failed to load menu items';
          setMenuItems([]);
          setError(message);
          toast.error(message);
        }
      } finally {
        if (cancelIfStale.active) {
          setLoadingMenuItems(false);
        }
      }
    };

    loadMenuItems();

    return () => {
      cancelIfStale.active = false;
    };
  }, [selectedStallId]);

  const updateField = (field) => (event) => {
    const value = field === 'available' ? event.target.checked : event.target.value;
    setForm((current) => ({ ...current, [field]: value }));
    setErrors((current) => ({ ...current, [field]: '' }));
    setSuccessMessage('');
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setSuccessMessage('');

    if (!selectedStallId) {
      const message = 'Select a stall before adding a menu item.';
      setError(message);
      toast.error(message);
      return;
    }

    const validationErrors = validateMenuItemForm(form);
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      toast.error('Please complete all menu item details.');
      return;
    }

    try {
      setSubmitting(true);
      setErrors({});

      const payload = buildMenuItemPayload(form);
      let response;
      if (editingItem) {
        response = await updateStaffMenuItem(selectedStallId, editingItem.id, payload);
      } else {
        response = await createStaffMenuItem(selectedStallId, payload);
      }

      const itemName = response.data?.itemName || payload.itemName;
      setSuccessMessage(editingItem ? `Updated menu item: ${itemName}` : `Created menu item: ${itemName}`);
      toast.success(editingItem ? `Menu item updated: ${itemName}` : `Menu item created: ${itemName}`);
      setForm(initialFormState);
      setEditingItem(null);
      const updatedResponse = await getStaffMenuItems(selectedStallId);
      setMenuItems(updatedResponse.data ?? []);
    } catch (submitError) {
      const message = submitError.response?.data?.message || 'Failed to save menu item';
      setError(message);
      toast.error(message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleEditItem = (item) => {
    setEditingItem(item);
    setForm({
      itemName: item.itemName,
      description: item.description,
      price: item.price,
      category: item.category,
      available: item.available,
    });
    setError('');
    setSuccessMessage('');
  };

  const handleDeleteItem = async (item) => {
    if (!selectedStallId || !item?.id) {
      return;
    }

    try {
      setSubmitting(true);
      await deleteStaffMenuItem(selectedStallId, item.id);
      const updatedResponse = await getStaffMenuItems(selectedStallId);
      setMenuItems(updatedResponse.data ?? []);
      setSuccessMessage(`Deleted menu item: ${item.itemName}`);
      toast.success(`Menu item deleted: ${item.itemName}`);
      if (editingItem?.id === item.id) {
        setEditingItem(null);
        setForm(initialFormState);
      }
    } catch (deleteError) {
      const message = deleteError.response?.data?.message || 'Failed to delete menu item';
      setError(message);
      toast.error(message);
    } finally {
      setSubmitting(false);
    }
  };

  const selectedStall = stalls.find((stall) => String(stall.stallId) === String(selectedStallId));

  return (
<div className="card" id="menu-editor">
      <div className="flex items-center gap-3 mb-6">
        <div className="w-12 h-12 rounded-xl bg-amber-100 text-amber-700 flex items-center justify-center">
          <PlusCircle size={24} />
        </div>
        <div>
          <h2 className="text-2xl font-semibold text-gray-900">My Stall Menu</h2>
          <p className="text-sm text-gray-600">Manage menu items for your assigned stall only.</p>
        </div>
      </div>

      {loadingStalls ? (
        <div className="py-8 flex justify-center">
          <Loader2 className="h-6 w-6 animate-spin text-blue-600" />
        </div>
      ) : (
        <div className="grid lg:grid-cols-[1.2fr_0.8fr] gap-6">
          <div>
            <h3 className="text-xl font-semibold text-gray-900 mb-4">Menu Item Details</h3>

            {error && (
              <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
                {error}
              </div>
            )}

            {successMessage && (
              <div className="mb-4 rounded-lg border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700">
                {successMessage}
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">

              <div className="form-group">
                <label htmlFor="itemName" className="form-label">
                  Item Name
                </label>
                <input
                  id="itemName"
                  type="text"
                  className="input-field"
                  placeholder="e.g. Chicken Rice"
                  value={form.itemName}
                  onChange={updateField('itemName')}
                  disabled={submitting}
                />
                {errors.itemName && <p className="mt-2 text-sm text-red-600">{errors.itemName}</p>}
              </div>

              <div className="form-group">
                <label htmlFor="description" className="form-label">
                  Description
                </label>
                <textarea
                  id="description"
                  rows="4"
                  className="input-field resize-none"
                  placeholder="Short description of the item"
                  value={form.description}
                  onChange={updateField('description')}
                  disabled={submitting}
                />
                {errors.description && (
                  <p className="mt-2 text-sm text-red-600">{errors.description}</p>
                )}
              </div>

              <div className="grid md:grid-cols-2 gap-4">
                <div className="form-group">
                  <label htmlFor="price" className="form-label">
                    Price
                  </label>
                  <input
                    id="price"
                    type="number"
                    step="0.01"
                    min="0.01"
                    className="input-field"
                    placeholder="0.00"
                    value={form.price}
                    onChange={updateField('price')}
                    disabled={submitting}
                  />
                  {errors.price && <p className="mt-2 text-sm text-red-600">{errors.price}</p>}
                </div>

                <div className="form-group">
                  <label htmlFor="category" className="form-label">
                    Category
                  </label>
                  <input
                    id="category"
                    type="text"
                    className="input-field"
                    placeholder="e.g. Meals"
                    value={form.category}
                    onChange={updateField('category')}
                    disabled={submitting}
                  />
                  {errors.category && <p className="mt-2 text-sm text-red-600">{errors.category}</p>}
                </div>
              </div>

              <label className="flex items-center gap-3 rounded-lg border border-gray-200 px-4 py-3 text-sm text-gray-700">
                <input
                  type="checkbox"
                  checked={form.available}
                  onChange={updateField('available')}
                  disabled={submitting}
                />
                Item is available for ordering
              </label>

              <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                <button
                  type="submit"
                  className="btn-primary w-full flex items-center justify-center gap-2"
                  disabled={submitting || stalls.length === 0}
                >
                  {submitting ? <Loader2 className="h-4 w-4 animate-spin" /> : <Store className="h-4 w-4" />}
                  {submitting ? (editingItem ? 'Saving Item...' : 'Creating Item...') : (editingItem ? 'Update Menu Item' : 'Create Menu Item')}
                </button>
                {editingItem && (
                  <button
                    type="button"
                    onClick={() => {
                      setEditingItem(null);
                      setForm(initialFormState);
                      setError('');
                      setSuccessMessage('');
                    }}
                    className="btn-secondary w-full sm:w-auto"
                    disabled={submitting}
                  >
                    Cancel Edit
                  </button>
                )}
              </div>
            </form>
          </div>

          <div className="space-y-6">
            <div className="card bg-gradient-to-br from-amber-50 to-orange-100 border border-amber-100">
              <h3 className="text-xl font-semibold text-gray-900 mb-3">Selected Stall</h3>
              {selectedStall ? (
                <div className="space-y-2 text-sm text-gray-700">
                  <p><span className="font-semibold">Name:</span> {selectedStall.stallName}</p>
                  <p><span className="font-semibold">Vendor:</span> {selectedStall.vendorName}</p>
                  <p><span className="font-semibold">Hours:</span> {selectedStall.operatingHours}</p>
                </div>
              ) : (
                <p className="text-sm text-gray-700">
                  No stall selected. Create a stall first if you do not have one yet.
                </p>
              )}
            </div>

            <div className="card bg-white border border-gray-200">
              <h3 className="text-xl font-semibold text-gray-900 mb-3">Menu Items</h3>
              {loadingMenuItems ? (
                <div className="py-6 text-center text-sm text-gray-500">Loading menu items…</div>
              ) : menuItems.length === 0 ? (
                <div className="rounded-lg border border-dashed border-gray-200 bg-gray-50 p-4 text-sm text-gray-600">
                  No menu items available for this stall yet.
                </div>
              ) : (
                <div className="space-y-3">
                  {menuItems.map((item) => (
                    <div key={item.id} className="rounded-xl border border-gray-200 p-3 bg-gray-50">
                      <div className="flex items-start justify-between gap-3">
                        <div>
                          <p className="font-semibold text-gray-900">{item.itemName}</p>
                          <p className="text-sm text-gray-600">{item.category} • {item.available ? 'Available' : 'Unavailable'}</p>
                        </div>
                        <div className="text-right">
                          <p className="font-semibold text-gray-900">₱{Number(item.price).toFixed(2)}</p>
                        </div>
                      </div>
                      <p className="mt-2 text-sm text-gray-700">{item.description}</p>
                      <div className="mt-3 flex flex-wrap gap-2">
                        <button
                          type="button"
                          onClick={() => handleEditItem(item)}
                          className="btn-secondary text-sm"
                        >
                          Edit
                        </button>
                        <button
                          type="button"
                          onClick={() => handleDeleteItem(item)}
                          disabled={submitting}
                          className="btn-outline text-sm text-red-600 hover:bg-red-100"
                        >
                          Delete
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>

            <div className="card">
              <h3 className="text-xl font-semibold text-gray-900 mb-3">Related Actions</h3>
              <div className="space-y-2">
                <button 
                  type="button"
                  onClick={() => navigate('/staff/dashboard')}
                  className="block btn-secondary text-center w-full"
                >
                  Back to Staff Dashboard
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default StaffMenuItemManagementPage;
