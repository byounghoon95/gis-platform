import { apiGet } from './client';

export function getHealth() {
  return apiGet('/api/health');
}
