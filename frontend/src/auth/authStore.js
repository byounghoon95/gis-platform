import { create } from 'zustand';

const AUTH_STORAGE_KEY = 'gis-platform.auth';

export function isSessionValid(session) {
  return Boolean(
    session.accessToken &&
      session.expiresAt &&
      new Date(session.expiresAt).getTime() > Date.now(),
  );
}

export const useAuthStore = create((set) => ({
  ...loadStoredSession(),
  setSession: (loginResponse) => {
    const session = {
      accessToken: loginResponse.accessToken,
      tokenType: loginResponse.tokenType,
      expiresAt: new Date(
        Date.now() + loginResponse.expiresInSeconds * 1000,
      ).toISOString(),
      user: loginResponse.user,
    };

    saveSession(session);
    set(session);
  },
  logout: () => {
    clearSession();
    set(emptySession());
  },
}));

function loadStoredSession() {
  if (typeof window === 'undefined') {
    return emptySession();
  }

  try {
    const value = window.localStorage.getItem(AUTH_STORAGE_KEY);

    return value ? JSON.parse(value) : emptySession();
  } catch {
    return emptySession();
  }
}

function saveSession(session) {
  window.localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session));
}

function clearSession() {
  window.localStorage.removeItem(AUTH_STORAGE_KEY);
}

function emptySession() {
  return {
    accessToken: null,
    tokenType: null,
    expiresAt: null,
    user: null,
  };
}
