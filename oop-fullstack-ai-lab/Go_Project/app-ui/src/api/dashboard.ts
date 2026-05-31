import api from './apiClient';

export async function fetchDashboard() {
  const response = await api.get('/dashboard');
  return response.data;
}

export async function fetchAnalytics() {
  const response = await api.get('/analytics/summary');
  return response.data;
}
