import React from 'react';
import { useNavigate } from 'react-router-dom';
import { StatusBadge } from './CareerApplicationStatusCard';

export default function CareerApplicationTable({ applications }) {
  const navigate = useNavigate();

  if (!applications?.length) {
    return (
      <div className="rounded-lg border border-gray-200 bg-white p-10 text-center text-gray-600 shadow-sm">
        No career applications found.
      </div>
    );
  }

  return (
    <div className="overflow-hidden rounded-lg border border-gray-200 bg-white shadow-sm">
      <table className="min-w-full divide-y divide-gray-200">
        <thead className="bg-gray-50">
          <tr>
            <th className="px-6 py-3 text-left text-xs font-bold uppercase tracking-wide text-gray-600">Applicant</th>
            <th className="px-6 py-3 text-left text-xs font-bold uppercase tracking-wide text-gray-600">Email</th>
            <th className="px-6 py-3 text-left text-xs font-bold uppercase tracking-wide text-gray-600">Phone</th>
            <th className="px-6 py-3 text-left text-xs font-bold uppercase tracking-wide text-gray-600">Status</th>
            <th className="px-6 py-3 text-left text-xs font-bold uppercase tracking-wide text-gray-600">Submitted</th>
            <th className="px-6 py-3 text-right text-xs font-bold uppercase tracking-wide text-gray-600">Action</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-100">
          {applications.map((application) => (
            <tr key={application.id} className="hover:bg-gray-50">
              <td className="px-6 py-4 font-semibold text-gray-900">
                {application.firstname} {application.lastname}
              </td>
              <td className="px-6 py-4 text-gray-600">{application.email}</td>
              <td className="px-6 py-4 text-gray-600">{application.phoneNumber}</td>
              <td className="px-6 py-4">
                <StatusBadge status={application.status} />
              </td>
              <td className="px-6 py-4 text-gray-600">
                {application.submittedAt ? new Date(application.submittedAt).toLocaleDateString() : 'Not available'}
              </td>
              <td className="px-6 py-4 text-right">
                <button
                  onClick={() => navigate(`/admin/career-applications/${application.id}`)}
                  className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-bold text-white hover:bg-blue-700"
                >
                  Review
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
