import assert from 'node:assert/strict';

import { buildMenuItemPayload, validateMenuItemForm } from '../src/utils/menuItemForm.js';

const validInput = {
  itemName: '  Chicken Rice  ',
  description: '  Rice with chicken  ',
  price: ' 45.50 ',
  category: '  Meals ',
  available: 1,
};

assert.deepEqual(buildMenuItemPayload(validInput), {
  itemName: 'Chicken Rice',
  description: 'Rice with chicken',
  price: 45.5,
  category: 'Meals',
  available: true,
});

assert.deepEqual(validateMenuItemForm(validInput), {});

assert.deepEqual(validateMenuItemForm({
  itemName: '',
  description: '',
  price: '0',
  category: '',
}), {
  itemName: 'Item name is required.',
  description: 'Description is required.',
  price: 'Price must be greater than zero.',
  category: 'Category is required.',
});

console.log('menuItemForm tests passed');
