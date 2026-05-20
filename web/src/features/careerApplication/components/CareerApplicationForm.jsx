import React, { useState } from 'react';

const allowedResumeTypes = [
  'application/pdf',
  'application/msword',
  'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
];

export default function CareerApplicationForm({ onSubmit, submitting, disabled }) {
  const [phoneNumber, setPhoneNumber] = useState('');
  const [resume, setResume] = useState(null);
  const [coverLetter, setCoverLetter] = useState('');
  const [validationError, setValidationError] = useState('');

  const handleSubmit = async (event) => {
    event.preventDefault();
    setValidationError('');

    if (!phoneNumber.trim()) {
      setValidationError('Phone number is required.');
      return;
    }
    if (!resume) {
      setValidationError('Resume file is required.');
      return;
    }
    if (!allowedResumeTypes.includes(resume.type)) {
      setValidationError('Resume must be a PDF, DOC, or DOCX file.');
      return;
    }
    if (!coverLetter.trim()) {
      setValidationError('Cover letter is required.');
      return;
    }
    if (coverLetter.length > 5000) {
      setValidationError('Cover letter must not exceed 5000 characters.');
      return;
    }

    await onSubmit({ phoneNumber, resume, coverLetter });
  };

  return (
    <form onSubmit={handleSubmit} className="bg-white border border-gray-200 rounded-lg p-6 shadow-sm space-y-5">
      <div>
        <h1 className="text-3xl font-bold text-gray-900">Career Application</h1>
        <p className="mt-2 text-gray-600">Submit your agent application for admin review.</p>
      </div>

      {validationError && (
        <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {validationError}
        </div>
      )}

      <div>
        <label htmlFor="phoneNumber" className="block text-sm font-semibold text-gray-700 mb-2">
          Phone number
        </label>
        <input
          id="phoneNumber"
          value={phoneNumber}
          onChange={(event) => setPhoneNumber(event.target.value)}
          disabled={disabled || submitting}
          className="w-full rounded-lg border border-gray-300 px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
          placeholder="Enter your phone number"
          required
        />
      </div>

      <div>
        <label htmlFor="resume" className="block text-sm font-semibold text-gray-700 mb-2">
          Resume
        </label>
        <input
          id="resume"
          type="file"
          accept=".pdf,.doc,.docx,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document"
          onChange={(event) => setResume(event.target.files?.[0] || null)}
          disabled={disabled || submitting}
          className="w-full rounded-lg border border-gray-300 px-4 py-2 file:mr-4 file:rounded-md file:border-0 file:bg-blue-600 file:px-4 file:py-2 file:text-white disabled:bg-gray-100"
          required
        />
      </div>

      <div>
        <div className="flex justify-between gap-3">
          <label htmlFor="coverLetter" className="block text-sm font-semibold text-gray-700 mb-2">
            Cover letter
          </label>
          <span className="text-xs text-gray-500">{coverLetter.length}/5000</span>
        </div>
        <textarea
          id="coverLetter"
          value={coverLetter}
          onChange={(event) => setCoverLetter(event.target.value)}
          disabled={disabled || submitting}
          rows={8}
          maxLength={5000}
          className="w-full rounded-lg border border-gray-300 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100"
          placeholder="Tell us why you want to become a Fort Point Properties agent"
          required
        />
      </div>

      <button
        type="submit"
        disabled={disabled || submitting}
        className="w-full rounded-lg bg-blue-600 px-5 py-3 font-bold text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-60"
      >
        {submitting ? 'Submitting...' : 'Submit Application'}
      </button>
    </form>
  );
}
