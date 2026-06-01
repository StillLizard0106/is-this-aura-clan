export const formatCurrency = (amount, currency = 'PHP') => {
  if (amount === null || amount === undefined) return '';
  return new Intl.NumberFormat('en-PH', {
    style: 'currency',
    currency: currency,
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(amount);
};

export const parseCurrency = (amount) => {
  if (typeof amount === 'string') {
    return parseFloat(amount.replace(/[^0-9.-]+/g, ''));
  }
  return parseFloat(amount);
};
