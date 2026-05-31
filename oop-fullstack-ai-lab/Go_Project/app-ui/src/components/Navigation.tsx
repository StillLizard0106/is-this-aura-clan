import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navigation() {
  const { user, logout } = useAuth();

  return (
    <nav className="nav-bar">
      <div className="nav-brand">Go Grow Glow</div>
      <div className="nav-links">
        {user ? (
          <>
            <Link to="/dashboard">Dashboard</Link>
            <Link to="/tasks">Tasks</Link>
            <Link to="/habits">Habits</Link>
            <button className="link-button" onClick={logout}>Logout</button>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}
      </div>
    </nav>
  );
}
