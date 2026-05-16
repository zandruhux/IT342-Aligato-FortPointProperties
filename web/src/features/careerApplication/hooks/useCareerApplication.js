import { useCallback, useEffect, useState } from 'react';
import careerApplicationApi from '../api/careerApplicationApi';

export function useCareerApplication() {
  const [application, setApplication] = useState(null);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const fetchMyApplication = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await careerApplicationApi.getMyCareerApplication();
      setApplication(data);
      return data;
    } catch (err) {
      setError(err.message || 'Failed to load career application');
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const submitApplication = useCallback(async ({ phoneNumber, resume, coverLetter }) => {
    setSubmitting(true);
    setError('');
    setSuccess('');

    try {
      const formData = new FormData();
      formData.append('phoneNumber', phoneNumber);
      formData.append('resume', resume);
      formData.append('coverLetter', coverLetter);

      const data = await careerApplicationApi.submitCareerApplication(formData);
      setApplication(data);
      setSuccess('Application submitted successfully.');
      return data;
    } catch (err) {
      setError(err.message || 'Failed to submit career application');
      throw err;
    } finally {
      setSubmitting(false);
    }
  }, []);

  useEffect(() => {
    fetchMyApplication().catch(() => {});
  }, [fetchMyApplication]);

  return {
    application,
    loading,
    submitting,
    error,
    success,
    setError,
    submitApplication,
    fetchMyApplication,
  };
}

export default useCareerApplication;
