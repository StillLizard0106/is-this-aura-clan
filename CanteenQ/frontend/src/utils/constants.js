export const ORDER_STATUS = {
  PENDING: 'PENDING',
  PREPARING: 'PREPARING',
  READY: 'READY',
  COMPLETED: 'COMPLETED',
  CANCELLED: 'CANCELLED',
  UNCLAIMED: 'UNCLAIMED',
};

export const ORDER_STATUS_LABELS = {
  PENDING: 'Pending',
  PREPARING: 'Preparing',
  READY: 'Ready for Pickup',
  COMPLETED: 'Completed',
  CANCELLED: 'Cancelled',
  UNCLAIMED: 'Unclaimed',
};

export const ORDER_STATUS_COLORS = {
  PENDING: 'badge-info',
  PREPARING: 'badge-warning',
  READY: 'badge-success',
  COMPLETED: 'badge-success',
  CANCELLED: 'badge-error',
  UNCLAIMED: 'badge-error',
};

export const USER_ROLES = {
  STUDENT: 'STUDENT',
  STAFF: 'STAFF',
};

export const VALIDATION_RULES = {
  EMAIL_PATTERN: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
  PASSWORD_MIN_LENGTH: 6,
  PICKUP_SLOT_MIN_MINUTES: 15,
};

export const API_ERRORS = {
  UNAUTHORIZED: 'Unauthorized. Please log in again.',
  FORBIDDEN: 'You do not have permission to access this.',
  NOT_FOUND: 'Resource not found.',
  CONFLICT: 'This resource already exists or has a conflict.',
  SERVER_ERROR: 'Server error. Please try again later.',
};

export const NOTIFICATION_TYPES = {
  SUCCESS: 'success',
  ERROR: 'error',
  WARNING: 'warning',
  INFO: 'info',
};
