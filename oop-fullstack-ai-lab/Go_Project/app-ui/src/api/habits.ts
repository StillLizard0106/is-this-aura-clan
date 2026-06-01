import api from './apiClient';
import { HabitItem } from '../types';

export async function fetchHabits(): Promise<HabitItem[]> {
  const response = await api.get('/habits');
  return response.data;
}

export async function createHabit(habit: Omit<HabitItem, 'id'>): Promise<HabitItem> {
  const response = await api.post('/habits', habit);
  return response.data;
}

export async function checkInHabit(id: number): Promise<void> {
  await api.patch(`/habits/${id}/check-in`);
}
