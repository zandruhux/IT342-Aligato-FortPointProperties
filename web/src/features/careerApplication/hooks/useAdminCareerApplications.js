import { useCallback, useEffect, useState } from 'react';
import careerApplicationApi from '../api/careerApplicationApi';

export function useAdminCareerApplications(applicationId) {
  const [applications, setApplications] = useState([]);
  const [application, setApplication] = useState(null);
  const [loading, setLoading] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchApplications = useCallback(async (status) => {
    setLoading(true);
    setError('');
    try {
      const data = await careerApplicationApi.getAllCareerApplications(status);
      setApplications(data || []);
      return data;
    } catch (err) {
      setError(err.message || 'Failed to load career applications');
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchApplication = useCallback(async (id = applicationId) => {
    if (!id) return null;
    setLoading(true);
    setError('');
    try {
      const data = await careerApplicationApi.getCareerApplicationById(id);
      setApplication(data);
      return data;
    } catch (err) {
      setError(err.message || 'Failed to load career application');
      throw err;
    } finally {
      setLoading(false);
    }
  }, [applicationId]);

  const acceptApplication = useCallback(async (id, remarks) => {
    setActionLoading(true);
    setError('');
    try {
      const data = await careerApplicationApi.acceptCareerApplication(id, remarks);
      setApplication(data);
      setApplications((prev) => prev.map((item) => (item.id === id ? data : item)));
      return data;
    } catch (err) {
      setError(err.message || 'Failed to accept career application');
      throw err;
    } finally {
      setActionLoading(false);
    }
  }, []);

  const rejectApplication = useCallback(async (id, remarks) => {
    setActionLoading(true);
    setError('');
    try {
      const data = await careerApplicationApi.rejectCareerApplication(id, remarks);
      setApplication(data);
      setApplications((prev) => prev.map((item) => (item.id === id ? data : item)));
      return data;
    } catch (err) {
      setError(err.message || 'Failed to reject career application');
      throw err;
    } finally {
      setActionLoading(false);
    }
  }, []);

  useEffect(() => {
    if (applicationId) {
      fetchApplication(applicationId).catch(() => {});
    }
  }, [applicationId, fetchApplication]);

  return {
    applications,
    application,
    loading,
    actionLoading,
    error,
    setError,
    fetchApplications,
    fetchApplication,
    acceptApplication,
    rejectApplication,
  };
}

export default useAdminCareerApplications;
