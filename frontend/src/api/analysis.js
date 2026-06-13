import { apiGet, apiPost } from './client';

function authHeaders(accessToken) {
  return {
    Authorization: `Bearer ${accessToken}`,
  };
}

export function getNearby(locationId, radius, accessToken) {
  return apiGet(`/api/locations/${locationId}/nearby?radius=${radius}`, {
    headers: authHeaders(accessToken),
  });
}

export function getLatestScore(locationId, accessToken) {
  return apiGet(`/api/locations/${locationId}/score`, {
    headers: authHeaders(accessToken),
  });
}

export function analyzeLocation(locationId, radius, accessToken) {
  return apiPost(
    `/api/locations/${locationId}/analysis?radius=${radius}`,
    undefined,
    {
      headers: authHeaders(accessToken),
    },
  );
}
