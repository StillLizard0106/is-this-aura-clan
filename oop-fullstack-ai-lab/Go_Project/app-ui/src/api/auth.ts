import api from './apiClient';
import { AuthResponse, UserProfile } from '../types';

export async function login(email: string, password: string): Promise<AuthResponse> {
  const response = await api.post('/auth/login', { email, password });
  return response.data;
}

export async function register(email: string, username: string, password: string): Promise<AuthResponse> {
  const response = await api.post('/auth/register', { email, username, password });
  return response.data;
}

export async function getProfile(): Promise<UserProfile> {
  const response = await api.get('/users/me');
  return response.data;
}
