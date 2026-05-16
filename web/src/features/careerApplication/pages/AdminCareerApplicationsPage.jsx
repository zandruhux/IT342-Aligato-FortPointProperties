import React, { useEffect, useState } from 'react';
import { AdminSidebar } from '../../../shared/components/layout';
import CareerApplicationTable from '../components/CareerApplicationTable';
import useAdminCareerApplications from '../hooks/useAdminCareerApplications';

export default function AdminCareerApplicationsPage() {
  const [status, setStatus] = useState('');
  const { applications, loading, error, fetchApplications } = useAdminCareerApplications();

  useEffect(() => {
    fetchApplications(status || undefined).catch(() => {});
  }, [status, fetchApplications]);

  return (
    <div className="flex min-h-screen bg-gray-50">
      <AdminSidebar />
      <main className="ml-64 flex-1 py-12">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="mb-8 flex flex-wrap items-end justify-between gap-4">
            <div>
              <h1 className="text-4xl font-bold text-gray-900">Career Applications</h1>
              <p className="mt-2 text-gray-600">Review registered users applying to become agents.</p>
            </div>
            <select
              value={status}
              onChange={(event) => setStatus(event.target.value)}
              className="rounded-lg border border-gray-300 px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">All statuses</option>
              <option value="PENDING">Pending</option>
              <option value="ACCEPTED">Accepted</option>
              <option value="REJECTED">Rejected</option>
            </select>
          </div>

          {error && (
            <div className="mb-5 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-red-700">
              {error}
            </div>
          )}

          {loading ? (
            <div className="rounded-lg border border-gray-200 bg-white p-10 text-center text-gray-600 shadow-sm">
              Loading applications...
            </div>
          ) : (
            <CareerApplicationTable applications={applications} />
          )}
        </div>
      </main>
    </div>
  );
}
