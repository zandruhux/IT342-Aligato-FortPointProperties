import React, { useState } from 'react';
import careerApplicationApi from '../api/careerApplicationApi';
import { StatusBadge } from './CareerApplicationStatusCard';

export default function CareerApplicationDetails({ application }) {
  const [resumeError, setResumeError] = useState('');
  const [openingResume, setOpeningResume] = useState(false);

  if (!application) {
    return null;
  }

  const handleOpenResume = async () => {
    setOpeningResume(true);
    setResumeError('');
    try {
      const signedUrl = await careerApplicationApi.getCareerApplicationResumeUrl(application.id);
      window.open(signedUrl, '_blank', 'noopener,noreferrer');
    } catch (error) {
      setResumeError(error.message || 'Failed to open resume.');
    } finally {
      setOpeningResume(false);
    }
  };

  return (
    <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
      <div className="mb-6 flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">
            {application.firstname} {application.lastname}
          </h1>
          <p className="mt-1 text-gray-600">{application.email}</p>
        </div>
        <StatusBadge status={application.status} />
      </div>

      <dl className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div>
          <dt className="text-sm font-bold text-gray-700">Phone number</dt>
          <dd className="mt-1 text-gray-900">{application.phoneNumber}</dd>
        </div>
        <div>
          <dt className="text-sm font-bold text-gray-700">Submitted</dt>
          <dd className="mt-1 text-gray-900">{application.submittedAt ? new Date(application.submittedAt).toLocaleString() : 'Not available'}</dd>
        </div>
        <div>
          <dt className="text-sm font-bold text-gray-700">Resume</dt>
          <dd className="mt-1">
            <button
              type="button"
              onClick={handleOpenResume}
              disabled={openingResume}
              className="font-semibold text-blue-600 hover:underline disabled:opacity-60"
            >
              {application.resumeOriginalFilename || 'Open resume'}
            </button>
            {resumeError && <p className="mt-2 text-sm text-red-600">{resumeError}</p>}
          </dd>
        </div>
        {application.reviewedAt && (
          <div>
            <dt className="text-sm font-bold text-gray-700">Reviewed</dt>
            <dd className="mt-1 text-gray-900">{new Date(application.reviewedAt).toLocaleString()}</dd>
          </div>
        )}
      </dl>

      <div className="mt-6">
        <h2 className="text-lg font-bold text-gray-900">Cover Letter</h2>
        <p className="mt-3 whitespace-pre-wrap rounded-lg bg-gray-50 p-4 leading-7 text-gray-700">
          {application.coverLetter}
        </p>
      </div>

      {application.adminRemarks && (
        <div className="mt-6">
          <h2 className="text-lg font-bold text-gray-900">Admin Remarks</h2>
          <p className="mt-3 rounded-lg bg-gray-50 p-4 text-gray-700">{application.adminRemarks}</p>
        </div>
      )}
    </div>
  );
}
