import { afterEach, describe, expect, it, vi } from 'vitest';

import { ApiError } from './client';
import { loginAdmin } from './auth';

afterEach(() => {
  vi.unstubAllGlobals();
});

describe('loginAdmin', () => {
  it('posts credentials to the login endpoint', async () => {
    const response = {
      accessToken: 'token',
      tokenType: 'Bearer',
      expiresInSeconds: 3600,
      user: {
        id: 1,
        email: 'admin@example.com',
        name: 'Admin',
        role: 'ADMIN',
      },
    };

    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => response,
    });

    vi.stubGlobal('fetch', fetchMock);

    await expect(
      loginAdmin({ email: 'admin@example.com', password: 'admin1234' }),
    ).resolves.toEqual(response);
    expect(fetchMock).toHaveBeenCalledWith('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        email: 'admin@example.com',
        password: 'admin1234',
      }),
    });
  });

  it('throws the backend login error message', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue({
        ok: false,
        status: 401,
        json: async () => ({
          message: 'Invalid credentials',
        }),
      }),
    );

    await expect(
      loginAdmin({ email: 'admin@example.com', password: 'wrong' }),
    ).rejects.toEqual(new ApiError('Invalid credentials', 401));
  });
});
