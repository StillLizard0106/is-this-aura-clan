import assert from 'node:assert/strict';

import { normalizeUserRole } from '../src/utils/userRole.js';

assert.equal(normalizeUserRole('ADMIN'), 'ADMIN');
assert.equal(normalizeUserRole('STAFF'), 'STAFF');
assert.equal(normalizeUserRole(null), 'STUDENT');
assert.equal(normalizeUserRole(null, 'staff@canteen.local'), 'STAFF');
assert.equal(normalizeUserRole(null, 'admin@canteen.local'), 'ADMIN');
