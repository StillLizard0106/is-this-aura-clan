export interface Task {
  id: number;
  title: string;
  notes?: string;
  dueDate?: string;
  priority?: string;
  completed: boolean;
}

export interface Habit {
  id: number;
  name: string;
  cadence?: string;
  description?: string;
  streak: number;
  completedToday: boolean;
}

export interface WellnessEntry {
  id: number;
  date: string;
  moodScore: number;
  energyScore: number;
  stressScore: number;
  notes?: string;
}

export interface DashboardSummary {
  totalTasks: number;
  completedTasks: number;
  totalHabits: number;
  activeStreaks: number;
  wellnessBalance: number;
  scoreMessage: string;
}
