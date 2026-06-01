import api from './apiClient';
import { TaskItem } from '../types';

export async function fetchTasks(): Promise<TaskItem[]> {
  const response = await api.get('/tasks');
  return response.data;
}

export async function createTask(task: Omit<TaskItem, 'id'>): Promise<TaskItem> {
  const response = await api.post('/tasks', task);
  return response.data;
}

export async function completeTask(id: number): Promise<TaskItem> {
  const response = await api.patch(`/tasks/${id}/complete`);
  return response.data;
}

export async function deleteTask(id: number): Promise<void> {
  await api.delete(`/tasks/${id}`);
}
