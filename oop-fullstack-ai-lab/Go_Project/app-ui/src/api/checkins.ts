import api from './apiClient';
import { CheckInItem } from '../types';

export async function submitCheckIn(checkIn: Omit<CheckInItem, 'id' | 'checkInDate'>): Promise<CheckInItem> {
  const response = await api.post('/check-ins', checkIn);
  return response.data;
}

export async function fetchCheckIns(): Promise<CheckInItem[]> {
  const response = await api.get('/check-ins/history');
  return response.data;
}
