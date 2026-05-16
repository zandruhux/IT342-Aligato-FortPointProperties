import React, { useState } from 'react';

export default function ApplicationActionButtons({ application, onAccept, onReject, loading }) {
  const [remarks, setRemarks] = useState('');
  const isReviewed = application?.status && application.status !== 'PENDING';

  return (
    <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
      <h2 className="text-xl font-bold text-gray-900 mb-4">Review Decision</h2>
      <textarea
        value={remarks}
        onChange={(event) => setRemarks(event.target.value)}
        disabled={isReviewed || loading}
        rows={4}
        maxLength={1000}
        className="w-full rounded-lg border border-gray-300 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
        placeholder="Optional admin remarks"
      />
      <div className="mt-4 flex flex-wrap gap-3">
        <button
          onClick={() => onAccept(remarks)}
          disabled={isReviewed || loading}
          className="rounded-lg bg-emerald-600 px-5 py-2 font-bold text-white hover:bg-emerald-700 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {loading ? 'Saving...' : 'Accept'}
        </button>
        <button
          onClick={() => onReject(remarks)}
          disabled={isReviewed || loading}
          className="rounded-lg bg-red-600 px-5 py-2 font-bold text-white hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-60"
        >
          Reject
        </button>
      </div>
      {isReviewed && (
        <p className="mt-3 text-sm text-gray-600">This application has already been reviewed.</p>
      )}
    </div>
  );
}
