import { FormEvent, useEffect, useState } from 'react';
import Navigation from '../components/Navigation';
import TaskCard from '../components/TaskCard';
import { fetchTasks, createTask, completeTask, deleteTask } from '../api/tasks';
import { TaskItem } from '../types';

const defaultTask = { title: '', description: '', priority: 'MEDIUM', status: 'PENDING', dueDate: '' };

export default function TasksPage() {
  const [tasks, setTasks] = useState<TaskItem[]>([]);
  const [taskForm, setTaskForm] = useState(defaultTask);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    reloadTasks();
  }, []);

  const reloadTasks = async () => {
    setLoading(true);
    const data = await fetchTasks();
    setTasks(data);
    setLoading(false);
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    await createTask({
      title: taskForm.title,
      description: taskForm.description,
      priority: taskForm.priority,
      status: taskForm.status,
      dueDate: taskForm.dueDate || undefined,
    });
    setTaskForm(defaultTask);
    await reloadTasks();
  };

  return (
    <div className="page-shell">
      <Navigation />
      <main className="page-content">
        <h1>Tasks</h1>
        <section className="form-panel">
          <h2>Create Task</h2>
          <form onSubmit={handleSubmit} className="stacked-form">
            <label>
              Title
              <input value={taskForm.title} onChange={(e) => setTaskForm({ ...taskForm, title: e.target.value })} required />
            </label>
            <label>
              Description
              <textarea value={taskForm.description} onChange={(e) => setTaskForm({ ...taskForm, description: e.target.value })} />
            </label>
            <label>
              Priority
              <select value={taskForm.priority} onChange={(e) => setTaskForm({ ...taskForm, priority: e.target.value as TaskItem['priority'] })}>
                <option value="LOW">Low</option>
                <option value="MEDIUM">Medium</option>
                <option value="HIGH">High</option>
              </select>
            </label>
            <label>
              Due Date
              <input type="date" value={taskForm.dueDate} onChange={(e) => setTaskForm({ ...taskForm, dueDate: e.target.value })} />
            </label>
            <button className="primary-button" type="submit">Add Task</button>
          </form>
        </section>
        <section className="list-panel">
          <h2>Today's Tasks</h2>
          {loading ? (
            <p>Loading tasks...</p>
          ) : tasks.length === 0 ? (
            <p>No tasks due today.</p>
          ) : (
            <div className="card-grid">
              {tasks.map((task) => (
                <TaskCard key={task.id} task={task} onComplete={async (id) => { await completeTask(id); await reloadTasks(); }} />
              ))}
            </div>
          )}
        </section>
      </main>
    </div>
  );
}
