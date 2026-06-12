import { Navigate, useLocation } from 'react-router-dom';

import { isSessionValid, useAuthStore } from './authStore';

export default function ProtectedRoute({ children }) {
  const location = useLocation();
  const session = useAuthStore((state) => ({
    accessToken: state.accessToken,
    expiresAt: state.expiresAt,
  }));

  if (!isSessionValid(session)) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return children;
}
