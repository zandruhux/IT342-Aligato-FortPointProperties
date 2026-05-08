import axiosInstance from '../../../shared/utils/api';
import { API_ENDPOINTS } from '../../../shared/utils/constants';

/**
 * Profile API Module
 * Centralized API layer for user profile endpoints
 */

export const getProfile = async () => {
  try {
    const response = await axiosInstance.get(API_ENDPOINTS.AUTH.PROFILE);
    if (response.data.success && response.data.data) {
      return response.data.data;
    }
    throw new Error('Failed to fetch profile');
  } catch (error) {
    throw error.response?.data?.error || 'Failed to fetch profile';
  }
};

/**
 * Update user profile
 * Note: Backend endpoint may need to be added (e.g., PUT /api/v1/auth/profile)
 */
export const updateProfile = async (profileData) => {
  try {
    // This endpoint may need to be added to the backend
    const response = await axiosInstance.put(
      `${API_ENDPOINTS.AUTH.PROFILE}`,
      profileData
    );
    if (response.data.success && response.data.data) {
      return response.data.data;
    }
    throw new Error('Failed to update profile');
  } catch (error) {
    throw error.response?.data?.error || 'Failed to update profile';
  }
};
