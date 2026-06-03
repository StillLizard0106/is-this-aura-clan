import assert from 'node:assert/strict';

import { getDashboardFetchOutcome } from '../src/utils/dashboardFetch.js';

assert.deepEqual(
  getDashboardFetchOutcome({ response: { status: 403, data: { message: 'Forbidden' } } }),
  { type: 'forbidden', message: 'Forbidden' }
);

assert.deepEqual(
  getDashboardFetchOutcome(new Error('Network Error')),
  { type: 'offline', message: 'Backend is offline. Showing dashboard in local mode.' }
);
