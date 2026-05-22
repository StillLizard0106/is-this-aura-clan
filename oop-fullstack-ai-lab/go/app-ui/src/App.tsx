import { useEffect, useMemo, useState } from 'react';
import { createHabit, createTask, createWellness, getDashboard, getHabits, getTasks, getWellness, completeTask, trackHabit } from './api';
import { DashboardSummary, Habit, Task, WellnessEntry } from './types';

const tabs = ['Dashboard', 'Tasks', 'Habits', 'Wellness'] as const;

type Tab = (typeof tabs)[number];

function App() {
  const [activeTab, setActiveTab] = useState<Tab>('Dashboard');
  const [dashboard, setDashboard] = useState<DashboardSummary | null>(null);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [habits, setHabits] = useState<Habit[]>([]);
  const [wellness, setWellness] = useState<WellnessEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const loadAll = async () => {
    setLoading(true);
    setError(null);
    try {
      const [dashboardData, tasksData, habitsData, wellnessData] = await Promise.all([
        getDashboard(),
        getTasks(),
        getHabits(),
        getWellness(),
      ]);
      setDashboard(dashboardData);
      setTasks(tasksData);
      setHabits(habitsData);
      setWellness(wellnessData);
    } catch (err) {
      setError((err as Error).message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAll();
  }, []);

  const activeContent = useMemo(() => {
    if (loading) {
      return <div className="panel">Loading...</div>;
    }
    if (error) {
      return <div className="panel error">{error}</div>;
    }

    switch (activeTab) {
      case 'Dashboard':
        return <DashboardPanel dashboard={dashboard} />;
      case 'Tasks':
        return (
          <TaskPanel
            tasks={tasks}
            onCreate={async (title, dueDate, priority) => {
              await createTask({ title, dueDate, priority });
              await loadAll();
            }}
            onComplete={async (id) => {
              await completeTask(id);
              await loadAll();
            }}
          />
        );
      case 'Habits':
        return (
          <HabitPanel
            habits={habits}
            onCreate={async (name, cadence, description) => {
              await createHabit({ name, cadence, description });
              await loadAll();
            }}
            onTrack={async (id) => {
              await trackHabit(id);
              await loadAll();
            }}
          />
        );
      case 'Wellness':
        return (
          <WellnessPanel
            entries={wellness}
            onCreate={async (date, moodScore, energyScore, stressScore, notes) => {
              await createWellness({ date, moodScore, energyScore, stressScore, notes });
              await loadAll();
            }}
          />
        );
      default:
        return null;
    }
  }, [activeTab, dashboard, error, habits, loading, tasks, wellness]);

  return (
    <div className="app-shell">
      <header>
        <div>
          <h1>Go Grow Glow</h1>
          <p>Balanced productivity, habit, and wellness tracking.</p>
        </div>
        <div className="tabs">
          {tabs.map((tab) => (
            <button key={tab} className={tab === activeTab ? 'active' : ''} onClick={() => setActiveTab(tab)}>
              {tab}
            </button>
          ))}
        </div>
      </header>
      {activeContent}
    </div>
  );
}

function DashboardPanel({ dashboard }: { dashboard: DashboardSummary | null }) {
  if (!dashboard) {
    return <div className="panel">No dashboard data yet.</div>;
  }

  return (
    <div className="panel grid">
      <div className="card">
        <h2>Tasks</h2>
        <p>{dashboard.completedTasks} / {dashboard.totalTasks} completed</p>
      </div>
      <div className="card">
        <h2>Habits</h2>
        <p>{dashboard.totalHabits} habits</p>
        <p>{dashboard.activeStreaks} active streaks</p>
      </div>
      <div className="card">
        <h2>Wellness</h2>
        <p>Balance: {dashboard.wellnessBalance.toFixed(1)}</p>
      </div>
      <div className="card wide">
        <h2>Momentum</h2>
        <p>{dashboard.scoreMessage}</p>
      </div>
    </div>
  );
}

function TaskPanel({
  tasks,
  onCreate,
  onComplete,
}: {
  tasks: Task[];
  onCreate: (title: string, dueDate: string, priority: string) => Promise<void>;
  onComplete: (id: number) => Promise<void>;
}) {
  const [title, setTitle] = useState('');
  const [priority, setPriority] = useState('Medium');
  const [dueDate, setDueDate] = useState('');

  return (
    <div className="panel">
      <div className="form-row">
        <input value={title} onChange={(e) => setTitle(e.target.value)} placeholder="New task title" />
        <input type="date" value={dueDate} onChange={(e) => setDueDate(e.target.value)} />
        <select value={priority} onChange={(e) => setPriority(e.target.value)}>
          <option>Low</option>
          <option>Medium</option>
          <option>High</option>
        </select>
        <button disabled={!title} onClick={async () => { await onCreate(title, dueDate, priority); setTitle(''); setDueDate(''); }}>Add task</button>
      </div>
      <div className="list">
        {tasks.length === 0 ? <div className="empty">No tasks yet.</div> : null}
        {tasks.map((task) => (
          <div key={task.id} className={`item ${task.completed ? 'completed' : ''}`}>
            <div>
              <strong>{task.title}</strong>
              <div className="meta">{task.dueDate || 'No due date'} · {task.priority || 'No priority'}</div>
            </div>
            <button disabled={task.completed} onClick={() => onComplete(task.id)}>{task.completed ? 'Done' : 'Complete'}</button>
          </div>
        ))}
      </div>
    </div>
  );
}

function HabitPanel({
  habits,
  onCreate,
  onTrack,
}: {
  habits: Habit[];
  onCreate: (name: string, cadence?: string, description?: string) => Promise<void>;
  onTrack: (id: number) => Promise<void>;
}) {
  const [name, setName] = useState('');
  const [cadence, setCadence] = useState('Daily');
  const [description, setDescription] = useState('');

  return (
    <div className="panel">
      <div className="form-row">
        <input value={name} onChange={(e) => setName(e.target.value)} placeholder="Habit name" />
        <select value={cadence} onChange={(e) => setCadence(e.target.value)}>
          <option>Daily</option>
          <option>Weekly</option>
        </select>
        <input value={description} onChange={(e) => setDescription(e.target.value)} placeholder="Description" />
        <button disabled={!name} onClick={async () => { await onCreate(name, cadence, description); setName(''); setDescription(''); }}>Add habit</button>
      </div>
      <div className="list">
        {habits.length === 0 ? <div className="empty">No habits yet.</div> : null}
        {habits.map((habit) => (
          <div key={habit.id} className="item">
            <div>
              <strong>{habit.name}</strong>
              <div className="meta">{habit.cadence} · Streak: {habit.streak}</div>
            </div>
            <button onClick={() => onTrack(habit.id)}>{habit.completedToday ? 'Tracked' : 'Track'}</button>
          </div>
        ))}
      </div>
    </div>
  );
}

function WellnessPanel({
  entries,
  onCreate,
}: {
  entries: WellnessEntry[];
  onCreate: (date: string, moodScore: number, energyScore: number, stressScore: number, notes: string) => Promise<void>;
}) {
  const [date, setDate] = useState(new Date().toISOString().slice(0, 10));
  const [moodScore, setMoodScore] = useState(7);
  const [energyScore, setEnergyScore] = useState(7);
  const [stressScore, setStressScore] = useState(4);
  const [notes, setNotes] = useState('');

  return (
    <div className="panel">
      <div className="form-column">
        <label>
          Date
          <input type="date" value={date} onChange={(e) => setDate(e.target.value)} />
        </label>
        <label>
          Mood
          <input type="range" min="1" max="10" value={moodScore} onChange={(e) => setMoodScore(Number(e.target.value))} />
          <span>{moodScore}</span>
        </label>
        <label>
          Energy
          <input type="range" min="1" max="10" value={energyScore} onChange={(e) => setEnergyScore(Number(e.target.value))} />
          <span>{energyScore}</span>
        </label>
        <label>
          Stress
          <input type="range" min="1" max="10" value={stressScore} onChange={(e) => setStressScore(Number(e.target.value))} />
          <span>{stressScore}</span>
        </label>
        <label>
          Notes
          <textarea value={notes} onChange={(e) => setNotes(e.target.value)} placeholder="Reflection or recovery notes" />
        </label>
        <button onClick={async () => { await onCreate(date, moodScore, energyScore, stressScore, notes); setNotes(''); }}>Save wellness</button>
      </div>
      <div className="list">
        {entries.length === 0 ? <div className="empty">No wellness entries yet.</div> : null}
        {entries.map((entry) => (
          <div key={entry.id} className="item">
            <div>
              <strong>{entry.date}</strong>
              <div className="meta">Mood: {entry.moodScore} · Energy: {entry.energyScore} · Stress: {entry.stressScore}</div>
              {entry.notes ? <p>{entry.notes}</p> : null}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default App;
