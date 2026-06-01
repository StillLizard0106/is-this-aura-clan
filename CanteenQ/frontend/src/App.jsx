import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

// Components
import Navbar from './components/shared/Navbar';
import PrivateRoute from './components/common/PrivateRoute';

// Pages - Auth
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';

// Pages - Student
import StudentDashboardPage from './pages/student/DashboardPage';
import OrderPlacementPage from './pages/student/OrderPlacementPage';
import OrderConfirmationPage from './pages/student/OrderConfirmationPage';
import OrderHistoryPage from './pages/student/OrderHistoryPage';

// Pages - Staff
import StaffDashboardPage from './pages/staff/StaffDashboardPage';
import StaffQueuePage from './pages/staff/StaffQueuePage';
import StaffReportingPage from './pages/staff/StaffReportingPage';

function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <ToastContainer
        position="bottom-right"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
      />
      <Routes>
        {/* Auth Routes */}
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Student Routes */}
        <Route
          path="/"
          element={
            <PrivateRoute requiredRole="STUDENT">
              <StudentDashboardPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/dashboard"
          element={
            <PrivateRoute requiredRole="STUDENT">
              <StudentDashboardPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/stall/:stallId/menu"
          element={
            <PrivateRoute requiredRole="STUDENT">
              <OrderPlacementPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/order/:orderId/confirmation"
          element={
            <PrivateRoute requiredRole="STUDENT">
              <OrderConfirmationPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/orders"
          element={
            <PrivateRoute requiredRole="STUDENT">
              <OrderHistoryPage />
            </PrivateRoute>
          }
        />

        {/* Staff Routes */}
        <Route
          path="/staff/dashboard"
          element={
            <PrivateRoute requiredRole="STAFF">
              <StaffDashboardPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/staff/orders"
          element={
            <PrivateRoute requiredRole="STAFF">
              <StaffQueuePage />
            </PrivateRoute>
          }
        />
        <Route
          path="/staff/reporting"
          element={
            <PrivateRoute requiredRole="STAFF">
              <StaffReportingPage />
            </PrivateRoute>
          }
        />

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
