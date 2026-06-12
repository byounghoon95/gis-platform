import { afterEach, describe, expect, it, vi } from 'vitest';

import { isSessionValid } from './authStore';

describe('isSessionValid', () => {
  afterEach(() => {
    vi.useRealTimers();
  });

  it('accepts sessions with an unexpired token', () => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date('2026-06-11T00:00:00Z'));

    expect(
      isSessionValid({
        accessToken: 'token',
        expiresAt: '2026-06-11T01:00:00Z',
      }),
    ).toBe(true);
  });

  it('rejects missing or expired tokens', () => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date('2026-06-11T00:00:00Z'));

    expect(isSessionValid({ accessToken: null, expiresAt: null })).toBe(false);
    expect(
      isSessionValid({
        accessToken: 'token',
        expiresAt: '2026-06-10T23:59:59Z',
      }),
    ).toBe(false);
  });
});
