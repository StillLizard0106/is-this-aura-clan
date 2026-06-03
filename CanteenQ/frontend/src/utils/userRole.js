const DEMO_ADMIN_EMAIL = 'admin@canteen.local';
const DEMO_STAFF_EMAILS = new Set(['staff@canteen.local', 'staff@canteenq.local']);

export const normalizeUserRole = (role, email = '') => {
  if (role === 'ADMIN' || role === 'STAFF') {
    return role;
  }

  const normalizedEmail = String(email || '').trim().toLowerCase();
  if (normalizedEmail === DEMO_ADMIN_EMAIL || normalizedEmail.startsWith('admin@')) {
    return 'ADMIN';
  }

  if (DEMO_STAFF_EMAILS.has(normalizedEmail) || normalizedEmail.startsWith('staff@')) {
    return 'STAFF';
  }

  return role || 'STUDENT';
};
