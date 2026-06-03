import assert from 'node:assert/strict';

import { buildStallPayload, validateStallForm } from '../src/utils/stallForm.js';

const validInput = {
  stallName: '  Pizza Stall  ',
  vendorName: '  Tony\'s Pizzeria ',
  operatingHours: ' 10:00 AM - 2:00 PM ',
};

const payload = buildStallPayload(validInput);

assert.deepEqual(payload, {
  stallName: 'Pizza Stall',
  vendorName: "Tony's Pizzeria",
  operatingHours: '10:00 AM - 2:00 PM',
});

assert.deepEqual(validateStallForm(validInput), {});

assert.deepEqual(validateStallForm({
  stallName: '',
  vendorName: ' ',
  operatingHours: '',
}), {
  stallName: 'Stall name is required.',
  vendorName: 'Vendor name is required.',
  operatingHours: 'Operating hours are required.',
});

console.log('stallForm tests passed');
