import { useQuery } from '@tanstack/react-query';
import { NavLink, Route, Routes } from 'react-router-dom';

import { getHealth } from './api/health';
import ProtectedRoute from './auth/ProtectedRoute';
import { useAuthStore } from './auth/authStore';
import LoginPage from './pages/LoginPage';

function DashboardPage() {
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);
  const healthQuery = useQuery({
    queryKey: ['health'],
    queryFn: getHealth,
    refetchInterval: 30000,
  });

  return (
    <main className="mx-auto flex min-h-screen max-w-7xl flex-col gap-6 px-6 py-6">
      <header className="flex flex-col gap-4 border-b border-slate-200 pb-5 md:flex-row md:items-center md:justify-between">
        <div>
          <p className="text-sm font-semibold uppercase tracking-wide text-sky-700">
            GIS Platform
          </p>
          <h1 className="text-3xl font-semibold text-slate-950">
            Location Analysis Dashboard
          </h1>
        </div>
        <nav className="flex gap-2 text-sm font-medium">
          <NavLink
            to="/"
            className={({ isActive }) =>
              `rounded-md px-3 py-2 ${
                isActive
                  ? 'bg-slate-950 text-white'
                  : 'text-slate-700 hover:bg-slate-100'
              }`
            }
          >
            Dashboard
          </NavLink>
          <button
            className="rounded-md px-3 py-2 text-slate-700 hover:bg-slate-100"
            type="button"
            onClick={logout}
          >
            Sign out
          </button>
        </nav>
      </header>

      <section className="rounded-lg border border-slate-200 bg-white px-5 py-4 shadow-sm">
        <p className="text-sm text-slate-600">
          Signed in as{' '}
          <span className="font-semibold text-slate-950">
            {user?.name ?? user?.email}
          </span>
          {user?.role ? (
            <span className="ml-2 rounded-full bg-sky-100 px-2 py-1 text-xs font-semibold text-sky-800">
              {user.role}
            </span>
          ) : null}
        </p>
      </section>

      <section className="grid gap-4 md:grid-cols-[1.2fr_0.8fr]">
        <div className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
          <h2 className="text-lg font-semibold text-slate-950">
            Candidate Locations
          </h2>
          <p className="mt-2 text-sm leading-6 text-slate-600">
            Frontend foundation is ready for candidate location workflows. Map,
            scoring, upload, and auth modules will be added in later tasks.
          </p>
          <div className="mt-5 grid gap-3 sm:grid-cols-3">
            <Metric label="Tracked locations" value="0" />
            <Metric label="Analysis radius" value="300m" />
            <Metric label="Backend endpoint" value="/api/health" />
          </div>
        </div>

        <div className="rounded-lg border border-slate-200 bg-white p-5 shadow-sm">
          <h2 className="text-lg font-semibold text-slate-950">
            Backend Health
          </h2>
          <HealthStatus
            isLoading={healthQuery.isLoading}
            isError={healthQuery.isError}
            status={healthQuery.data?.status}
            timestamp={healthQuery.data?.timestamp}
          />
        </div>
      </section>
    </main>
  );
}

function Metric({ label, value }) {
  return (
    <div className="rounded-md border border-slate-200 bg-slate-50 p-4">
      <p className="text-xs font-medium uppercase tracking-wide text-slate-500">
        {label}
      </p>
      <p className="mt-2 text-xl font-semibold text-slate-950">{value}</p>
    </div>
  );
}

function HealthStatus({
  isLoading,
  isError,
  status,
  timestamp,
}) {
  if (isLoading) {
    return <p className="mt-4 text-sm text-slate-600">Checking backend...</p>;
  }

  if (isError) {
    return (
      <div className="mt-4 rounded-md border border-amber-200 bg-amber-50 p-4 text-sm text-amber-900">
        Backend health check failed. Confirm `VITE_API_BASE_URL` points to the
        running API.
      </div>
    );
  }

  return (
    <div className="mt-4 space-y-3">
      <div className="flex items-center justify-between rounded-md border border-emerald-200 bg-emerald-50 p-4">
        <span className="text-sm font-medium text-emerald-950">Status</span>
        <span className="rounded-full bg-emerald-600 px-3 py-1 text-sm font-semibold text-white">
          {status ?? 'UNKNOWN'}
        </span>
      </div>
      <p className="text-sm text-slate-600">
        Last response:{' '}
        <span className="font-medium text-slate-900">
          {timestamp ? new Date(timestamp).toLocaleString() : 'not available'}
        </span>
      </p>
    </div>
  );
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <DashboardPage />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
}
