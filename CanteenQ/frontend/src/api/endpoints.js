import client from './axios-client';

// Auth APIs
export const verifyToken = () => client.get('/auth/verify');
export const getProfile = () => client.get('/protected/profile');

// Catalog APIs
export const getStalls = () => client.get('/stalls');
export const getMenuItems = (stallId) => client.get(`/stalls/${stallId}/menu-items`);

// Order APIs (Student)
export const placeOrder = (orderData) => client.post('/orders', orderData);
export const getMyOrders = () => client.get('/orders/my');
export const getOrderDetail = (orderId) => client.get(`/orders/${orderId}`);
export const cancelOrder = (orderId) => client.delete(`/orders/${orderId}`);
export const getOrderHistory = () => client.get('/orders/my');

// Order Notifications (SSE)
export const subscribeToOrderUpdates = (callback, errorCallback) => {
  const token = localStorage.getItem('firebaseToken');
  const baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';
  if (!token) {
    return { close: () => {} };
  }

  const eventSource = new EventSource(
    `${baseURL}/orders/notifications?access_token=${encodeURIComponent(token)}`
  );

  eventSource.onmessage = (event) => {
    try {
      const notification = JSON.parse(event.data);
      callback(notification);
    } catch (error) {
      console.error('Error parsing notification:', error);
    }
  };

  eventSource.onerror = (error) => {
    console.error('SSE connection error:', error);
    if (errorCallback) errorCallback(error);
    eventSource.close();
  };

  return eventSource;
};

// Staff APIs
export const getStaffDashboard = () => client.get('/staff/dashboard');
export const getMyStalls = () => client.get('/staff/assignments');
export const getAllStaffStalls = () => client.get('/staff/stalls');
export const createStaffStall = (payload) => client.post('/staff/stalls', payload);
export const updateStaffStall = (stallId, payload) => client.put(`/staff/stalls/${stallId}`, payload);
export const deleteStaffStall = (stallId) => client.delete(`/staff/stalls/${stallId}`);
export const getStaffMenuItems = (stallId) => client.get(`/staff/stalls/${stallId}/menu-items`);
export const createStaffMenuItem = (stallId, payload) => client.post(`/staff/stalls/${stallId}/menu-items`, payload);
export const updateStaffMenuItem = (stallId, menuItemId, payload) => client.put(`/staff/stalls/${stallId}/menu-items/${menuItemId}`, payload);
export const deleteStaffMenuItem = (stallId, menuItemId) => client.delete(`/staff/stalls/${stallId}/menu-items/${menuItemId}`);
export const getAdminStallAssignments = (stallId) => client.get(`/admin/stalls/${stallId}/assignments`);
export const assignStaffToStall = (stallId, payload) => client.post(`/admin/stalls/${stallId}/assignments`, payload);
export const removeStaffFromStall = (stallId, staffId) => client.delete(`/admin/stalls/${stallId}/assignments/${staffId}`);
export const getStaffOrders = (params = {}) => 
  client.get('/staff/orders', { params });
export const getStaffOrderDetail = (orderId) => client.get(`/staff/orders/${orderId}`);
export const updateOrderStatus = (orderId, status) =>
  client.patch(`/staff/orders/${orderId}`, { status });
export const getOrderAuditTrail = (orderId) => client.get(`/staff/orders/${orderId}/audit`);
export const markOrderUnclaimed = (orderId) => client.patch(`/staff/orders/${orderId}/unclaimed`);

// Reporting APIs
export const getReportingDaily = (params = {}) =>
  client.get('/reporting/daily', { params });

export const getReportingStalls = (params = {}) =>
  client.get('/reporting/stalls', { params });

// Health API
export const checkHealth = () => client.get('/health');
