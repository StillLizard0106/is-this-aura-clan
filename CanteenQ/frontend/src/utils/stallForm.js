export const buildStallPayload = ({ stallName, vendorName, operatingHours }) => ({
  stallName: stallName.trim(),
  vendorName: vendorName.trim(),
  operatingHours: operatingHours.trim(),
});

export const validateStallForm = ({ stallName, vendorName, operatingHours }) => {
  const errors = {};

  if (!stallName.trim()) {
    errors.stallName = 'Stall name is required.';
  }

  if (!vendorName.trim()) {
    errors.vendorName = 'Vendor name is required.';
  }

  if (!operatingHours.trim()) {
    errors.operatingHours = 'Operating hours are required.';
  }

  return errors;
};
