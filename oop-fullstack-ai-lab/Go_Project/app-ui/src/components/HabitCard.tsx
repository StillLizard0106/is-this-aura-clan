import { HabitItem } from '../types';

interface HabitCardProps {
  habit: HabitItem;
  onCheckIn: (id: number) => void;
}

export default function HabitCard({ habit, onCheckIn }: HabitCardProps) {
  return (
    <div className="card">
      <div className="card-header">
        <h3>{habit.name}</h3>
        <span className="pill">{habit.frequency}</span>
      </div>
      <p>Target: {habit.targetCount} / day</p>
      <button className="primary-button" onClick={() => onCheckIn(habit.id)}>Check In</button>
    </div>
  );
}
