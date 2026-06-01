import { TaskItem } from '../types';

interface TaskCardProps {
  task: TaskItem;
  onComplete: (id: number) => void;
}

export default function TaskCard({ task, onComplete }: TaskCardProps) {
  return (
    <div className="card">
      <div className="card-header">
        <h3>{task.title}</h3>
        <span className="pill">{task.priority}</span>
      </div>
      <p>{task.description}</p>
      <div className="card-meta">
        <span>Status: {task.status}</span>
        {task.dueDate && <span>Due: {task.dueDate}</span>}
      </div>
      {task.status !== 'COMPLETED' && (
        <button className="primary-button" onClick={() => onComplete(task.id)}>Complete</button>
      )}
    </div>
  );
}
