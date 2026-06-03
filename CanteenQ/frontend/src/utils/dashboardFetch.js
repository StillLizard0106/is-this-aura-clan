export const getDashboardFetchOutcome = (error) => {
  const status = error?.response?.status;
  const message = error?.response?.data?.message || 'Failed to load dashboard';

  if (status === 401 || status === 403) {
    return {
      type: 'forbidden',
      message,
    };
  }

  return {
    type: 'offline',
    message: 'Backend is offline. Showing dashboard in local mode.',
  };
};
