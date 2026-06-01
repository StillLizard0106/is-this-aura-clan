import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { LogOut, Menu, User } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';

const Navbar = () => {
  const { user, userRole, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = React.useState(false);
  const homePath = userRole === 'STAFF' ? '/staff/dashboard' : '/dashboard';

  const handleLogout = async () => {
    try {
      await logout();
      navigate('/login');
    } catch (error) {
      console.error('Logout failed:', error);
    }
  };

  return (
    <nav className="bg-white shadow-md">
      <div className="container-custom">
        <div className="flex justify-between items-center h-16">
          <Link to={isAuthenticated ? homePath : '/'} className="text-2xl font-bold text-blue-600">
            🍽️ CanteenQ
          </Link>

          <div className="hidden md:flex items-center gap-4">
            {isAuthenticated ? (
              <>
                <span className="text-sm text-gray-600">
                  {user?.email} ({userRole})
                </span>
                {userRole === 'STAFF' && (
                  <Link to="/staff/dashboard" className="text-gray-600 hover:text-gray-900">
                    Staff Dashboard
                  </Link>
                )}
                {userRole === 'STUDENT' && (
                  <>
                    <Link to="/dashboard" className="text-gray-600 hover:text-gray-900">
                      Browse
                    </Link>
                    <Link to="/orders" className="text-gray-600 hover:text-gray-900">
                      My Orders
                    </Link>
                  </>
                )}
                <button
                  onClick={handleLogout}
                  className="btn-secondary flex items-center gap-2"
                >
                  <LogOut size={18} /> Logout
                </button>
              </>
            ) : (
              <>
                <Link to="/login" className="btn-secondary">
                  Login
                </Link>
                <Link to="/register" className="btn-primary">
                  Register
                </Link>
              </>
            )}
          </div>

          <button
            className="md:hidden"
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          >
            <Menu size={24} />
          </button>
        </div>

        {mobileMenuOpen && (
          <div className="md:hidden pb-4 space-y-2">
            {isAuthenticated ? (
              <>
                <div className="text-sm text-gray-600 p-2">
                  {user?.email} ({userRole})
                </div>
                {userRole === 'STAFF' && (
                  <Link
                    to="/staff/dashboard"
                    className="block px-4 py-2 text-gray-600 hover:bg-gray-100"
                  >
                    Staff Dashboard
                  </Link>
                )}
                {userRole === 'STUDENT' && (
                  <>
                    <Link
                      to="/dashboard"
                      className="block px-4 py-2 text-gray-600 hover:bg-gray-100"
                    >
                      Browse
                    </Link>
                    <Link
                      to="/orders"
                      className="block px-4 py-2 text-gray-600 hover:bg-gray-100"
                    >
                      My Orders
                    </Link>
                  </>
                )}
                <button
                  onClick={handleLogout}
                  className="w-full btn-secondary text-left"
                >
                  Logout
                </button>
              </>
            ) : (
              <>
                <Link to="/login" className="block btn-secondary w-full text-center">
                  Login
                </Link>
                <Link to="/register" className="block btn-primary w-full text-center">
                  Register
                </Link>
              </>
            )}
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
