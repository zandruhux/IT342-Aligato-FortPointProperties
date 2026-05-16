import React from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { AdminSidebar } from '../../../shared/components/layout';
import ApplicationActionButtons from '../components/ApplicationActionButtons';
import CareerApplicationDetails from '../components/CareerApplicationDetails';
import useAdminCareerApplications from '../hooks/useAdminCareerApplications';

export default function AdminCareerApplicationDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const {
    application,
    loading,
    actionLoading,
    error,
    acceptApplication,
    rejectApplication,
  } = useAdminCareerApplications(id);

  const handleAccept = async (remarks) => {
    await acceptApplication(id, remarks);
  };

  const handleReject = async (remarks) => {
    await rejectApplication(id, remarks);
  };

  return (
    <div className="flex min-h-screen bg-gray-50">
      <AdminSidebar />
      <main className="ml-64 flex-1 py-12">
        <div className="mx-auto max-w-5xl px-4 sm:px-6 lg:px-8">
          <button
            onClick={() => navigate('/admin/career-applications')}
            className="mb-6 rounded-lg border border-gray-300 bg-white px-4 py-2 font-semibold text-gray-700 hover:bg-gray-50"
          >
            Back to applications
          </button>

          {error && (
            <div className="mb-5 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-red-700">
              {error}
            </div>
          )}

          {loading ? (
            <div className="rounded-lg border border-gray-200 bg-white p-10 text-center text-gray-600 shadow-sm">
              Loading application...
            </div>
          ) : (
            <div className="space-y-6">
              <CareerApplicationDetails application={application} />
              <ApplicationActionButtons
                application={application}
                onAccept={handleAccept}
                onReject={handleReject}
                loading={actionLoading}
              />
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
