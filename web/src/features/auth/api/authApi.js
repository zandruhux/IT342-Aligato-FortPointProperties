import axiosInstance from '../../../shared/utils/api';
import { API_ENDPOINTS } from '../../../shared/utils/constants';

/**
 * Auth API - Centralized authentication endpoints
 */

export const authApi = {
  /**
   * Register a new user
   * @param {Object} userData - Registration data (firstname, lastname, email, password, confirmPassword)
   * @returns {Promise} Response with user and tokens
   */
  register: async (userData) => {
    try {
      const response = await axiosInstance.post(API_ENDPOINTS.AUTH.REGISTER, {
        firstname: userData.firstname,
        lastname: userData.lastname,
        email: userData.email,
        password: userData.password,
        confirmPassword: userData.confirmPassword || userData.password,
      });

      console.log('Register Response:', JSON.stringify(response.data, null, 2));

      // Handle success response
      if (response.data.success && response.data.data) {
        return response.data.data; // { user, accessToken, refreshToken }
      }

      throw new Error('Unexpected response format');
    } catch (error) {
      console.error('Register Error:', JSON.stringify(error.response?.data, null, 2));
      // Extract error from response
      if (error.response?.data?.error) {
        throw error.response.data.error;
      }
      throw {
        code: 'REGISTER_ERROR',
        message: error.message || 'Registration failed',
      };
    }
  },

  /**
   * Login user
   * @param {Object} credentials - Login data (email, password)
   * @returns {Promise} Response with user and tokens
   */
  login: async (credentials) => {
    try {
      const response = await axiosInstance.post(API_ENDPOINTS.AUTH.LOGIN, {
        email: credentials.email,
        password: credentials.password,
      });

      console.log('Login Response:', JSON.stringify(response.data, null, 2));

      // Handle success response
      if (response.data.success && response.data.data) {
        return response.data.data; // { user, accessToken, refreshToken }
      }

      throw new Error('Unexpected response format');
    } catch (error) {
      console.error('Login Error:', JSON.stringify(error.response?.data, null, 2));
      // Extract error from response
      if (error.response?.data?.error) {
        throw error.response.data.error;
      }
      throw {
        code: 'LOGIN_ERROR',
        message: error.message || 'Login failed',
      };
    }
  },

  /**
   * Get current user profile
   * @returns {Promise} User profile data
   */
  getProfile: async () => {
    try {
      const response = await axiosInstance.get(API_ENDPOINTS.AUTH.PROFILE);
      if (response.data.success && response.data.data) {
        return response.data.data;
      }
      throw new Error('Failed to fetch profile');
    } catch (error) {
      console.error('Get Profile Error:', error);
      throw {
        code: 'PROFILE_ERROR',
        message: error.message || 'Failed to fetch profile',
      };
    }
  },
};

export default authApi;
