import axios from 'axios';
import { authService, isUsingMock } from './firebaseConfig';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

const client = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor to inject token (Firebase or mock)
client.interceptors.request.use(
  async (config) => {
    try {
      if (isUsingMock) {
        // Mock Auth
        if (authService?.currentUser) {
          const token = await authService.getIdToken();
          config.headers.Authorization = `Bearer ${token}`;
        }
      } else {
        // Firebase Auth
        if (authService?.currentUser) {
          const token = await authService.currentUser.getIdToken();
          config.headers.Authorization = `Bearer ${token}`;
        }
      }
    } catch (error) {
      console.error('Error getting auth token:', error);
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for error handling
client.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Unauthorized - redirect to login
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default client;


