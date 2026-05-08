import { useState, useCallback } from 'react';
import { useAuthContext } from '../../../shared/context/AuthContext';
import authApi from '../api/authApi';

/**
 * useAuth hook - Manages authentication logic
 * Handles registration, login, and auth state updates
 */
export function useAuth() {
  const { login: authContextLogin } = useAuthContext();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  /**
   * Register a new user
   * @param {Object} userData - Registration form data
   * @returns {Promise<Object>} User data if successful
   */
  const register = useCallback(async (userData) => {
    setLoading(true);
    setError(null);

    try {
      const response = await authApi.register(userData);
      const userData_response = response.user || response.data || response;
      const { accessToken, refreshToken } = response;

      // Update global auth context
      authContextLogin(userData_response, { accessToken, refreshToken });

      return userData_response;
    } catch (err) {
      const errorMessage = err.message || 'Registration failed';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [authContextLogin]);

  /**
   * Login a user
   * @param {Object} credentials - Login form data (email, password)
   * @returns {Promise<Object>} User data if successful
   */
  const login = useCallback(async (credentials) => {
    setLoading(true);
    setError(null);

    try {
      const response = await authApi.login(credentials);
      const userData = response.user || response.data || response;
      const { accessToken, refreshToken } = response;

      // Update global auth context
      authContextLogin(userData, { accessToken, refreshToken });

      return userData;
    } catch (err) {
      const errorMessage = err.message || 'Login failed';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, [authContextLogin]);

  return {
    register,
    login,
    loading,
    error,
    setError, // Allows components to clear error messages
  };
}

export default useAuth;
