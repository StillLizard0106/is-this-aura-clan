import { FormEvent, useEffect, useState } from 'react';
import Navigation from '../components/Navigation';
import HabitCard from '../components/HabitCard';
import { fetchHabits, createHabit, checkInHabit } from '../api/habits';
import { HabitItem } from '../types';

const defaultHabit = { name: '', targetCount: 1, frequency: 'DAILY' };

export default function HabitsPage() {
  const [habits, setHabits] = useState<HabitItem[]>([]);
  const [habitForm, setHabitForm] = useState(defaultHabit);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    reloadHabits();
  }, []);

  const reloadHabits = async () => {
    setLoading(true);
    const data = await fetchHabits();
    setHabits(data);
    setLoading(false);
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    await createHabit(habitForm);
    setHabitForm(defaultHabit);
    await reloadHabits();
  };

  return (
    <div className="page-shell">
      <Navigation />
      <main className="page-content">
        <h1>Habits</h1>
        <section className="form-panel">
          <h2>Create Habit</h2>
          <form onSubmit={handleSubmit} className="stacked-form">
            <label>
              Name
              <input value={habitForm.name} onChange={(e) => setHabitForm({ ...habitForm, name: e.target.value })} required />
            </label>
            <label>
              Target Count
              <input type="number" min={1} value={habitForm.targetCount} onChange={(e) => setHabitForm({ ...habitForm, targetCount: Number(e.target.value) })} />
            </label>
            <button className="primary-button" type="submit">Add Habit</button>
          </form>
        </section>
        <section className="list-panel">
          <h2>Habit List</h2>
          {loading ? (
            <p>Loading habits...</p>
          ) : habits.length === 0 ? (
            <p>No habits created yet.</p>
          ) : (
            <div className="card-grid">
              {habits.map((habit) => (
                <HabitCard key={habit.id} habit={habit} onCheckIn={async (id) => { await checkInHabit(id); await reloadHabits(); }} />
              ))}
            </div>
          )}
        </section>
      </main>
    </div>
  );
}
