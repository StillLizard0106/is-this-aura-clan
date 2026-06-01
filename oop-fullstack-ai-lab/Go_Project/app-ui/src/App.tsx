import { Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import DashboardPage from './pages/DashboardPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import TasksPage from './pages/TasksPage';
import HabitsPage from './pages/HabitsPage';

function App() {
  const { user } = useAuth();

  return (
    <div className="app-shell">
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route path="/dashboard" element={user ? <DashboardPage /> : <Navigate to="/login" />} />
        <Route path="/tasks" element={user ? <TasksPage /> : <Navigate to="/login" />} />
        <Route path="/habits" element={user ? <HabitsPage /> : <Navigate to="/login" />} />
        <Route path="/" element={<Navigate to={user ? '/dashboard' : '/login'} />} />
      </Routes>
    </div>
  );
}

export default App;
