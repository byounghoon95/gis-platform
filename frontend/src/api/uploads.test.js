import { afterEach, describe, expect, it, vi } from 'vitest';

import { ApiError } from './client';
import { uploadCsvDataset } from './uploads';

afterEach(() => {
  vi.unstubAllGlobals();
});

describe('uploadCsvDataset', () => {
  it('posts a CSV file with the bearer token', async () => {
    const response = {
      totalRows: 2,
      insertedRows: 2,
      errors: [],
    };
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      json: async () => response,
    });

    vi.stubGlobal('fetch', fetchMock);

    await expect(
      uploadCsvDataset({
        datasetType: 'facilities',
        file: new File(['name\nSchool'], 'facilities.csv', {
          type: 'text/csv',
        }),
        accessToken: 'token',
      }),
    ).resolves.toEqual(response);

    expect(fetchMock).toHaveBeenCalledWith(
      'http://localhost:8080/api/admin/uploads/facilities',
      expect.objectContaining({
        method: 'POST',
        headers: {
          Accept: 'application/json',
          Authorization: 'Bearer token',
        },
        body: expect.any(FormData),
      }),
    );
  });

  it('returns row errors from validation responses', async () => {
    const response = {
      totalRows: 1,
      insertedRows: 0,
      errors: [
        {
          rowNumber: 2,
          field: 'latitude',
          message: 'must be between -90 and 90',
        },
      ],
    };

    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue({
        ok: false,
        status: 400,
        json: async () => response,
      }),
    );

    await expect(
      uploadCsvDataset({
        datasetType: 'transit-stops',
        file: new File(['bad'], 'transit.csv'),
        accessToken: 'token',
      }),
    ).resolves.toEqual(response);
  });

  it('throws non-validation API errors', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue({
        ok: false,
        status: 403,
        json: async () => ({ message: 'Forbidden' }),
      }),
    );

    await expect(
      uploadCsvDataset({
        datasetType: 'competitors',
        file: new File(['bad'], 'competitors.csv'),
        accessToken: 'token',
      }),
    ).rejects.toEqual(new ApiError('Forbidden', 403));
  });
});
