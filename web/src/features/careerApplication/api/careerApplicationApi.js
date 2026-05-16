import axiosInstance from '../../../shared/utils/api';
import { API_ENDPOINTS } from '../../../shared/utils/constants';

const unwrap = (response) => {
  if (response.data?.success) {
    return response.data.data;
  }
  return response.data;
};

const careerApplicationApi = {
  submitCareerApplication: async (formData) => {
    try {
      const response = await axiosInstance.post(API_ENDPOINTS.CAREER_APPLICATIONS.SUBMIT, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return unwrap(response);
    } catch (error) {
      throw error.response?.data?.error || { message: 'Failed to submit career application' };
    }
  },

  getMyCareerApplication: async () => {
    try {
      const response = await axiosInstance.get(API_ENDPOINTS.CAREER_APPLICATIONS.ME);
      return unwrap(response);
    } catch (error) {
      throw error.response?.data?.error || { message: 'Failed to fetch your career application' };
    }
  },

  getAllCareerApplications: async (status) => {
    try {
      const response = await axiosInstance.get(API_ENDPOINTS.CAREER_APPLICATIONS.ADMIN_ALL, {
        params: status ? { status } : {},
      });
      return unwrap(response) || [];
    } catch (error) {
      throw error.response?.data?.error || { message: 'Failed to fetch career applications' };
    }
  },

  getCareerApplicationById: async (id) => {
    try {
      const response = await axiosInstance.get(API_ENDPOINTS.CAREER_APPLICATIONS.ADMIN_BY_ID(id));
      return unwrap(response);
    } catch (error) {
      throw error.response?.data?.error || { message: 'Failed to fetch career application' };
    }
  },

  acceptCareerApplication: async (id, remarks = '') => {
    try {
      const response = await axiosInstance.patch(API_ENDPOINTS.CAREER_APPLICATIONS.ACCEPT(id), { remarks });
      return unwrap(response);
    } catch (error) {
      throw error.response?.data?.error || { message: 'Failed to accept career application' };
    }
  },

  rejectCareerApplication: async (id, remarks = '') => {
    try {
      const response = await axiosInstance.patch(API_ENDPOINTS.CAREER_APPLICATIONS.REJECT(id), { remarks });
      return unwrap(response);
    } catch (error) {
      throw error.response?.data?.error || { message: 'Failed to reject career application' };
    }
  },

  getCareerApplicationResumeUrl: async (id) => {
    try {
      const response = await axiosInstance.get(API_ENDPOINTS.CAREER_APPLICATIONS.RESUME(id));
      return unwrap(response);
    } catch (error) {
      throw error.response?.data?.error || { message: 'Failed to open resume' };
    }
  },
};

export const {
  submitCareerApplication,
  getMyCareerApplication,
  getAllCareerApplications,
  getCareerApplicationById,
  acceptCareerApplication,
  rejectCareerApplication,
  getCareerApplicationResumeUrl,
} = careerApplicationApi;

export default careerApplicationApi;
