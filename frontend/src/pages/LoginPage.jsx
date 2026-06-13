import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';

import { loginAdmin } from '../api/auth';
import { isSessionValid, useAuthStore } from '../auth/authStore';

export default function LoginPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const [email, setEmail] = useState('admin@example.com');
  const [password, setPassword] = useState('');
  const setSession = useAuthStore((state) => state.setSession);
  const accessToken = useAuthStore((state) => state.accessToken);
  const expiresAt = useAuthStore((state) => state.expiresAt);

  const loginMutation = useMutation({
    mutationFn: loginAdmin,
    onSuccess: (response) => {
      setSession(response);
      navigate(location.state?.from?.pathname ?? '/', { replace: true });
    },
  });

  if (isSessionValid({ accessToken, expiresAt })) {
    return <Navigate to="/" replace />;
  }

  function handleSubmit(event) {
    event.preventDefault();
    loginMutation.mutate({ email, password });
  }

  function handleDevSession() {
    setSession({
      accessToken: 'local-dev-token',
      tokenType: 'Bearer',
      expiresInSeconds: 60 * 60,
      user: {
        email: 'admin@example.com',
        name: 'Local Admin',
        role: 'ADMIN',
      },
    });
    navigate(location.state?.from?.pathname ?? '/', { replace: true });
  }

  return (
    <main className="flex min-h-screen items-center justify-center bg-slate-50 px-6 py-10">
      <section className="w-full max-w-md rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
        <div className="mb-6">
          <p className="text-sm font-semibold uppercase tracking-wide text-sky-700">
            GIS Platform
          </p>
          <h1 className="mt-2 text-2xl font-semibold text-slate-950">
            Admin Login
          </h1>
        </div>

        <form className="space-y-4" onSubmit={handleSubmit}>
          <label className="block">
            <span className="text-sm font-medium text-slate-700">Email</span>
            <input
              className="mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-slate-950 outline-none focus:border-sky-600 focus:ring-2 focus:ring-sky-100"
              type="email"
              autoComplete="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
            />
          </label>

          <label className="block">
            <span className="text-sm font-medium text-slate-700">Password</span>
            <input
              className="mt-1 w-full rounded-md border border-slate-300 px-3 py-2 text-slate-950 outline-none focus:border-sky-600 focus:ring-2 focus:ring-sky-100"
              type="password"
              autoComplete="current-password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
            />
          </label>

          {loginMutation.isError ? (
            <div className="rounded-md border border-rose-200 bg-rose-50 px-3 py-2 text-sm text-rose-900">
              {loginMutation.error.message}
            </div>
          ) : null}

          <button
            className="w-full rounded-md bg-slate-950 px-4 py-2 font-semibold text-white hover:bg-slate-800 disabled:cursor-not-allowed disabled:bg-slate-400"
            type="submit"
            disabled={loginMutation.isPending}
          >
            {loginMutation.isPending ? 'Signing in...' : 'Sign in'}
          </button>

          {import.meta.env.DEV ? (
            <button
              className="w-full rounded-md border border-slate-300 px-4 py-2 font-semibold text-slate-700 hover:bg-slate-50"
              type="button"
              onClick={handleDevSession}
            >
              Continue with local dev session
            </button>
          ) : null}
        </form>
      </section>
    </main>
  );
}
