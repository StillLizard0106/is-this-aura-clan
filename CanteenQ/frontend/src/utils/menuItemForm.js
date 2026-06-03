export const buildMenuItemPayload = ({
  itemName,
  description,
  price,
  category,
  available,
}) => ({
  itemName: itemName.trim(),
  description: description.trim(),
  price: Number.parseFloat(String(price).trim()),
  category: category.trim(),
  available: Boolean(available),
});

export const validateMenuItemForm = ({
  itemName,
  description,
  price,
  category,
}) => {
  const errors = {};
  const normalizedPrice = Number.parseFloat(String(price).trim());

  if (!itemName.trim()) {
    errors.itemName = 'Item name is required.';
  }

  if (!description.trim()) {
    errors.description = 'Description is required.';
  }

  if (!Number.isFinite(normalizedPrice) || normalizedPrice <= 0) {
    errors.price = 'Price must be greater than zero.';
  }

  if (!category.trim()) {
    errors.category = 'Category is required.';
  }

  return errors;
};
