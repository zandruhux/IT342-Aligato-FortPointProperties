import React from 'react';
import CareerApplicationForm from '../components/CareerApplicationForm';
import CareerApplicationStatusCard from '../components/CareerApplicationStatusCard';
import useCareerApplication from '../hooks/useCareerApplication';

export default function CareerApplicationPage() {
  const {
    application,
    loading,
    submitting,
    error,
    success,
    submitApplication,
  } = useCareerApplication();

  const hasPendingApplication = application?.status === 'PENDING';

  return (
    <div className="bg-gray-50 py-12">
      <div className="mx-auto grid max-w-6xl grid-cols-1 gap-8 px-4 sm:px-6 lg:grid-cols-[1.2fr_0.8fr] lg:px-8">
        <div>
          {error && (
            <div className="mb-5 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-red-700">
              {error}
            </div>
          )}
          {success && (
            <div className="mb-5 rounded-lg border border-emerald-200 bg-emerald-50 px-4 py-3 text-emerald-700">
              {success}
            </div>
          )}
          <CareerApplicationForm
            onSubmit={submitApplication}
            submitting={submitting}
            disabled={hasPendingApplication}
          />
          {hasPendingApplication && (
            <p className="mt-4 rounded-lg bg-amber-50 px-4 py-3 text-sm text-amber-800">
              You already have a pending application. You can submit again after it is reviewed.
            </p>
          )}
        </div>

        <div>
          {loading ? (
            <div className="rounded-lg border border-gray-200 bg-white p-6 text-gray-600 shadow-sm">Loading status...</div>
          ) : (
            <CareerApplicationStatusCard application={application} />
          )}
        </div>
      </div>
    </div>
  );
}
