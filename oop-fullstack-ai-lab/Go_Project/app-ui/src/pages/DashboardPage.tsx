import { useEffect, useState } from 'react';
import Navigation from '../components/Navigation';
import DashboardWidget from '../components/DashboardWidget';
import { fetchDashboard } from '../api/dashboard';

export default function DashboardPage() {
  const [dashboard, setDashboard] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchDashboard()
      .then(setDashboard)
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return <div>Loading dashboard...</div>;
  }

  return (
    <div className="page-shell">
      <Navigation />
      <main className="page-content">
        <h1>Daily Dashboard</h1>
        <div className="dashboard-grid">
          <DashboardWidget title="Today's Tasks">
            {dashboard.todayTasks.length === 0 ? (
              <p>No tasks scheduled for today.</p>
            ) : (
              <ul>
                {dashboard.todayTasks.map((task: any) => (
                  <li key={task.id}>{task.title} — {task.status}</li>
                ))}
              </ul>
            )}
          </DashboardWidget>
          <DashboardWidget title="Habit Progress">
            <div>Habits: {dashboard.habitSummary.totalHabits}</div>
            <div>Completed today: {dashboard.habitSummary.completedToday}</div>
            <div>Consistency: {dashboard.habitSummary.consistency}%</div>
          </DashboardWidget>
          <DashboardWidget title="Productivity Gauge">
            <div>Score: {dashboard.productivityScore}</div>
            {dashboard.dailyCheckIn ? (
              <p>{dashboard.dailyCheckIn.productivityLevel}</p>
            ) : (
              <p>No check-in yet.</p>
            )}
          </DashboardWidget>
          <DashboardWidget title="Current Streaks">
            {dashboard.currentStreaks.length === 0 ? (
              <p>No streaks yet.</p>
            ) : (
              <ul>
                {dashboard.currentStreaks.map((streak: any) => (
                  <li key={streak.habitName}>{streak.habitName}: {streak.streakDays} days</li>
                ))}
              </ul>
            )}
          </DashboardWidget>
        </div>
      </main>
    </div>
  );
}
