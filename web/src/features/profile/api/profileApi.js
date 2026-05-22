import axiosInstance from '../../../shared/utils/api';
import { API_ENDPOINTS } from '../../../shared/utils/constants';
import { normalizeApiError } from '../../../shared/utils/errors';
import { IMAGE_LIMITS, validateImageFile } from '../../../shared/utils/fileValidation';

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
    throw normalizeApiError(error, 'Failed to fetch profile');
  }
};

export const getCurrentUser = getProfile;

export const uploadProfileImage = async (file) => {
  try {
    validateImageFile(file, {
      maxSizeBytes: IMAGE_LIMITS.PROFILE,
      requiredMessage: 'Profile image is required',
      sizeMessage: 'Profile image size exceeds maximum limit',
      typeMessage: 'Invalid profile image type',
    });

    const formData = new FormData();
    formData.append('image', file);

    const response = await axiosInstance.put(API_ENDPOINTS.AUTH.PROFILE_IMAGE, formData);
    if (response.data.success && response.data.data) {
      return response.data.data;
    }
    throw new Error('Failed to upload profile image');
  } catch (error) {
    throw normalizeApiError(error, 'Failed to upload profile image');
  }
};

export const removeProfileImage = async () => {
  try {
    const response = await axiosInstance.delete(API_ENDPOINTS.AUTH.PROFILE_IMAGE);
    if (response.data.success && response.data.data) {
      return response.data.data;
    }
    throw new Error('Failed to remove profile image');
  } catch (error) {
    throw normalizeApiError(error, 'Failed to remove profile image');
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
    throw normalizeApiError(error, 'Failed to update profile');
  }
};
