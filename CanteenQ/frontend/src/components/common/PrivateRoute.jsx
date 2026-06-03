import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import LoadingSpinner from './LoadingSpinner';

const PrivateRoute = ({ children, requiredRole = null }) => {
  const { isAuthenticated, userRole, loading } = useAuth();
  const dashboardPath = userRole === 'STUDENT' ? '/dashboard' : '/staff/dashboard';

  if (loading) {
    return <LoadingSpinner />;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (requiredRole && userRole !== requiredRole && userRole !== 'ADMIN') {
    return <Navigate to={dashboardPath} replace />;
  }

  return children;
};

export default PrivateRoute;
