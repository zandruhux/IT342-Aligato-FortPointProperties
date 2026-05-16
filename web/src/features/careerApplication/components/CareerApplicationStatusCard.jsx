import React from 'react';

const statusStyles = {
  PENDING: 'bg-amber-100 text-amber-800 border-amber-200',
  ACCEPTED: 'bg-emerald-100 text-emerald-800 border-emerald-200',
  REJECTED: 'bg-red-100 text-red-800 border-red-200',
};

export function StatusBadge({ status }) {
  return (
    <span className={`inline-flex items-center rounded-full border px-3 py-1 text-xs font-bold ${statusStyles[status] || 'bg-gray-100 text-gray-700 border-gray-200'}`}>
      {status || 'NO APPLICATION'}
    </span>
  );
}

export default function CareerApplicationStatusCard({ application }) {
  if (!application) {
    return (
      <div className="bg-white border border-gray-200 rounded-lg p-6 shadow-sm">
        <h2 className="text-xl font-bold text-gray-900 mb-2">Application Status</h2>
        <p className="text-gray-600">No career application has been submitted yet.</p>
      </div>
    );
  }

  return (
    <div className="bg-white border border-gray-200 rounded-lg p-6 shadow-sm">
      <div className="flex flex-wrap items-center justify-between gap-3 mb-4">
        <h2 className="text-xl font-bold text-gray-900">Application Status</h2>
        <StatusBadge status={application.status} />
      </div>
      <dl className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm">
        <div>
          <dt className="font-semibold text-gray-700">Submitted</dt>
          <dd className="text-gray-600">{application.submittedAt ? new Date(application.submittedAt).toLocaleString() : 'Not available'}</dd>
        </div>
        <div>
        </div>
        {application.reviewedAt && (
          <div>
            <dt className="font-semibold text-gray-700">Reviewed</dt>
            <dd className="text-gray-600">{new Date(application.reviewedAt).toLocaleString()}</dd>
          </div>
        )}
        {application.adminRemarks && (
          <div className="sm:col-span-2">
            <dt className="font-semibold text-gray-700">Remarks</dt>
            <dd className="text-gray-600">{application.adminRemarks}</dd>
          </div>
        )}
      </dl>
    </div>
  );
}
