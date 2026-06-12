import { apiPost } from './client';

export function loginAdmin(credentials) {
  return apiPost('/api/auth/login', credentials);
}
