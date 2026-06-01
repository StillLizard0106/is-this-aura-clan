import React from 'react';
import { ORDER_STATUS_LABELS, ORDER_STATUS_COLORS } from '../../utils/constants';

const StatusBadge = ({ status }) => {
  const label = ORDER_STATUS_LABELS[status] || status;
  const colorClass = ORDER_STATUS_COLORS[status] || 'badge-info';

  return <span className={`badge ${colorClass}`}>{label}</span>;
};

export default StatusBadge;
