import { afterEach, describe, expect, it, vi } from 'vitest';

import { getHealth } from './health';

afterEach(() => {
  vi.unstubAllGlobals();
});

describe('getHealth', () => {
  it('calls the backend health endpoint', async () => {
    const response = {
      status: 'UP',
      timestamp: '2026-06-11T00:00:00Z',
    };

    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => response,
    });

    vi.stubGlobal('fetch', fetchMock);

    await expect(getHealth()).resolves.toEqual(response);
    expect(fetchMock).toHaveBeenCalledWith('http://localhost:8080/api/health', {
      headers: {
        Accept: 'application/json',
      },
    });
  });
});
