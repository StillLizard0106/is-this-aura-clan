export interface AuthResponse {
  token: string;
  email: string;
  username: string;
}

export interface UserProfile {
  email: string;
  username: string;
}

export interface TaskItem {
  id: number;
  title: string;
  description?: string;
  priority: 'LOW' | 'MEDIUM' | 'HIGH';
  status: 'PENDING' | 'COMPLETED' | 'OVERDUE';
  dueDate?: string;
}

export interface HabitItem {
  id: number;
  name: string;
  targetCount: number;
  frequency: string;
}

export interface CheckInItem {
  id: number;
  productivityLevel: 'RED' | 'YELLOW' | 'GREEN';
  moodNote?: string;
  energyLevel?: number;
  checkInDate: string;
}
