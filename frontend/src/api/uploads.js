import { ApiError, API_BASE_URL } from './client';

export const uploadDatasetOptions = [
  {
    value: 'facilities',
    label: 'Facilities',
    helperText: 'Demand anchors such as schools, offices, and hospitals.',
  },
  {
    value: 'competitors',
    label: 'Competitors',
    helperText: 'Nearby businesses competing with candidate locations.',
  },
  {
    value: 'transit-stops',
    label: 'Transit stops',
    helperText: 'Subway stations and bus stops used for accessibility scoring.',
  },
  {
    value: 'foot-traffic',
    label: 'Foot traffic',
    helperText: 'Hourly pedestrian counts used for demand signals.',
  },
];

const uploadPaths = Object.fromEntries(
  uploadDatasetOptions.map((option) => [
    option.value,
    `/api/admin/uploads/${option.value}`,
  ]),
);

export async function uploadCsvDataset({ datasetType, file, accessToken }) {
  const path = uploadPaths[datasetType];

  if (!path) {
    throw new ApiError('Select a supported dataset type.', 400);
  }

  const formData = new FormData();
  formData.append('file', file);

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: 'POST',
    headers: {
      Accept: 'application/json',
      Authorization: `Bearer ${accessToken}`,
    },
    body: formData,
  });

  const body = await readJson(response);

  if (!response.ok && !isUploadResponse(body)) {
    throw new ApiError(
      body?.message ?? `Upload failed with status ${response.status}`,
      response.status,
    );
  }

  return normalizeUploadResponse(body);
}

function isUploadResponse(body) {
  return (
    body &&
    typeof body.insertedRows === 'number' &&
    Array.isArray(body.errors)
  );
}

function normalizeUploadResponse(body) {
  return {
    totalRows: body?.totalRows ?? 0,
    insertedRows: body?.insertedRows ?? 0,
    errors: body?.errors ?? [],
  };
}

async function readJson(response) {
  try {
    return await response.json();
  } catch {
    return null;
  }
}
