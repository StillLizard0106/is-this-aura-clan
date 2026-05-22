import { DashboardSummary, Habit, Task, WellnessEntry } from './types';

const baseUrl = '/api';

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const response = await fetch(`${baseUrl}${path}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  if (!response.ok) {
    throw new Error(`API request failed: ${response.statusText}`);
  }
  return response.json();
}

export const getTasks = () => request<Task[]>('/tasks');
export const createTask = (payload: Partial<Task>) =>
  request<Task>('/tasks', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
export const completeTask = (id: number) => request<Task>(`/tasks/${id}/complete`, { method: 'PATCH' });

export const getHabits = () => request<Habit[]>('/habits');
export const createHabit = (payload: Partial<Habit>) =>
  request<Habit>('/habits', {
    method: 'POST',
    body: JSON.stringify(payload),
  });
export const trackHabit = (id: number) => request<Habit>(`/habits/${id}/track`, { method: 'PATCH' });

export const getWellness = () => request<WellnessEntry[]>('/wellness');
export const createWellness = (payload: Partial<WellnessEntry>) =>
  request<WellnessEntry>('/wellness', {
    method: 'POST',
    body: JSON.stringify(payload),
  });

export const getDashboard = () => request<DashboardSummary>('/dashboard');
