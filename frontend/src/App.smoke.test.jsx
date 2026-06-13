import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import React from 'react';
import { renderToString } from 'react-dom/server';
import { MemoryRouter } from 'react-router-dom';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

import App from './App';

const authState = vi.hoisted(() => ({
  current: emptyAuthState(),
}));

vi.mock('./auth/authStore', () => ({
  isSessionValid: (session) =>
    Boolean(
      session.accessToken &&
        session.expiresAt &&
        new Date(session.expiresAt).getTime() > Date.now(),
    ),
  useAuthStore: (selector) => selector(authState.current),
}));

const validAdminSession = {
  accessToken: 'test-token',
  tokenType: 'Bearer',
  expiresAt: '2099-01-01T00:00:00.000Z',
  user: {
    email: 'admin@example.com',
    name: 'Smoke Test Admin',
    role: 'ADMIN',
  },
};

beforeEach(() => {
  authState.current = emptyAuthState();
  vi.stubEnv('VITE_GOOGLE_MAPS_API_KEY', '');
});

afterEach(() => {
  vi.unstubAllEnvs();
});

describe('route smoke tests', () => {
  it('renders the login route', () => {
    const html = renderRoute('/login');

    expect(html).toContain('Admin Login');
    expect(html).toContain('admin@example.com');
    expect(html).toContain('type="password"');
    expect(html).toContain('Sign in');
  });

  it('renders the dashboard route for an authenticated admin', () => {
    authState.current = {
      ...emptyAuthState(),
      ...validAdminSession,
    };

    const html = renderRoute('/dashboard');

    expect(html).toContain('Location Analysis Dashboard');
    expect(html).toContain('Candidate locations');
    expect(html).toContain('Score range');
    expect(html).toContain('Smoke Test Admin');
  });

  it('shows the map missing-key state without testing Google Maps internals', () => {
    authState.current = {
      ...emptyAuthState(),
      ...validAdminSession,
    };

    const html = renderRoute('/dashboard');

    expect(html).toContain('Google Maps API key missing');
    expect(html).toContain('Set VITE_GOOGLE_MAPS_API_KEY');
  });
});

function renderRoute(route) {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: {
        retry: false,
      },
    },
  });

  return renderToString(
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[route]}>
        <App />
      </MemoryRouter>
    </QueryClientProvider>,
  );
}

function emptyAuthState() {
  return {
    accessToken: null,
    tokenType: null,
    expiresAt: null,
    user: null,
    logout: vi.fn(),
    setSession: vi.fn(),
  };
}
