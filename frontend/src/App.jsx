import { Navigate, Route, Routes } from 'react-router-dom';

import ProtectedRoute from './auth/ProtectedRoute';
import AdminUploadPage from './pages/AdminUploadPage';
import DashboardPage from './pages/DashboardPage';
import LocationDetailPage from './pages/LocationDetailPage';
import LoginPage from './pages/LoginPage';

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/dashboard"
        element={
          <ProtectedRoute>
            <DashboardPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/locations/:id"
        element={
          <ProtectedRoute>
            <LocationDetailPage />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/uploads"
        element={
          <ProtectedRoute requiredRole="ADMIN">
            <AdminUploadPage />
          </ProtectedRoute>
        }
      />
      <Route path="/" element={<Navigate to="/dashboard" replace />} />
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  );
}
