import { Link, Navigate, useLocation } from 'react-router-dom';

import { isSessionValid, useAuthStore } from './authStore';

export default function ProtectedRoute({ children, requiredRole }) {
  const location = useLocation();
  const accessToken = useAuthStore((state) => state.accessToken);
  const expiresAt = useAuthStore((state) => state.expiresAt);
  const user = useAuthStore((state) => state.user);

  if (!isSessionValid({ accessToken, expiresAt })) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  if (requiredRole && user?.role !== requiredRole) {
    return (
      <main className="flex min-h-screen items-center justify-center bg-zinc-100 px-6 py-10 text-zinc-950">
        <section className="w-full max-w-md rounded-lg border border-zinc-200 bg-white p-6 shadow-sm">
          <p className="text-sm font-semibold uppercase tracking-wide text-teal-700">
            Access blocked
          </p>
          <h1 className="mt-2 text-2xl font-semibold">Admin access required</h1>
          <p className="mt-3 text-sm leading-6 text-zinc-600">
            CSV dataset uploads are limited to administrator accounts.
          </p>
          <NavigateLink />
        </section>
      </main>
    );
  }

  return children;
}

function NavigateLink() {
  return (
    <Link
      className="mt-5 inline-flex rounded-md bg-zinc-950 px-4 py-2 text-sm font-semibold text-white hover:bg-zinc-800"
      to="/dashboard"
    >
      Return to dashboard
    </Link>
  );
}
