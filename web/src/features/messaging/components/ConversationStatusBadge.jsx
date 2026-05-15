import React from 'react';

const styles = {
  OPEN: 'bg-emerald-50 text-emerald-700 border-emerald-200',
  ASSIGNED: 'bg-blue-50 text-blue-700 border-blue-200',
  CLOSED: 'bg-gray-100 text-gray-700 border-gray-200',
};

export default function ConversationStatusBadge({ status }) {
  const label = status === 'OPEN' ? 'Open' : status === 'ASSIGNED' ? 'Assigned' : 'Closed';

  return (
    <span className={`inline-flex items-center rounded border px-2 py-1 text-xs font-semibold ${styles[status] || styles.CLOSED}`}>
      {label}
    </span>
  );
}
