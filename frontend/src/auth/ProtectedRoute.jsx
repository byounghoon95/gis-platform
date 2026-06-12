import { Navigate, useLocation } from 'react-router-dom';

import { isSessionValid, useAuthStore } from './authStore';

export default function ProtectedRoute({ children }) {
  const location = useLocation();
  const accessToken = useAuthStore((state) => state.accessToken);
  const expiresAt = useAuthStore((state) => state.expiresAt);

  if (!isSessionValid({ accessToken, expiresAt })) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return children;
}
