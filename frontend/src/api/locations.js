import { apiGet } from './client';

export function getAdminLocations(accessToken, filters = {}) {
  const params = new URLSearchParams();

  if (filters.businessType) {
    params.set('businessType', filters.businessType);
  }

  if (filters.keyword) {
    params.set('keyword', filters.keyword);
  }

  const query = params.toString();

  return apiGet(`/api/admin/locations${query ? `?${query}` : ''}`, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });
}

export function getAdminLocation(locationId, accessToken) {
  return apiGet(`/api/admin/locations/${locationId}`, {
    headers: {
      Authorization: `Bearer ${accessToken}`,
    },
  });
}
