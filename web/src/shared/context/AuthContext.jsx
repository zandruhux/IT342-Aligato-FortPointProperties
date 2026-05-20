import React, { useState, useCallback } from 'react';
import { hasDetailAccess, canEditProperty, canDeleteProperty, canCreateProperty } from '../utils/propertyHelpers';
import { getRolePermissions, isAdmin, isAgent, isRegisteredUser } from '../utils/roleRules';
import { AuthContext } from './AuthContextBase';

const normalizeStoredUser = (userData = {}) => ({
  id: userData.id,
  email: userData.email,
  firstname: userData.firstname,
  lastname: userData.lastname,
  role: userData.role || userData.roles?.[0] || localStorage.getItem('role') || 'USER',
});

const getInitialAuthState = () => {
  const token = localStorage.getItem('accessToken');
  const refreshToken = localStorage.getItem('refreshToken');
  const userStr = localStorage.getItem('user');

  if (!token || !userStr) {
    return {
      isLoggedIn: false,
      user: null,
      tokens: {
        accessToken: null,
        refreshToken: null,
      },
      authReady: true,
    };
  }

  try {
    return {
      isLoggedIn: true,
      user: normalizeStoredUser(JSON.parse(userStr)),
      tokens: {
        accessToken: token,
        refreshToken,
      },
      authReady: true,
    };
  } catch (error) {
    console.error('Failed to initialize auth from storage:', error);
    return {
      isLoggedIn: false,
      user: null,
      tokens: {
        accessToken: null,
        refreshToken: null,
      },
      authReady: true,
    };
  }
};

/**
 * AuthProvider - Global authentication provider
 * Wraps the app to provide authentication state and methods
 */
export function AuthProvider({ children }) {
  const [authState, setAuthState] = useState(getInitialAuthState);

  /**
   * Login - Sets auth state from login/register response
   * @param {Object} userData - User information (id, email, firstname, lastname, role)
   * @param {Object} tokens - Auth tokens (accessToken, refreshToken)
   */
  const login = useCallback((userData, tokens) => {
    const normalizedUser = userData?.user || userData?.data || userData || {};
    const normalizedTokens = tokens || {};

    setAuthState({
      isLoggedIn: true,
      user: normalizeStoredUser(normalizedUser),
      tokens: {
        accessToken: normalizedTokens.accessToken,
        refreshToken: normalizedTokens.refreshToken,
      },
      authReady: true,
    });

    // Persist to localStorage
    localStorage.setItem('accessToken', normalizedTokens.accessToken);
    localStorage.setItem('refreshToken', normalizedTokens.refreshToken);
    localStorage.setItem('user', JSON.stringify(normalizeStoredUser(normalizedUser)));
    localStorage.setItem('role', normalizedUser.role || normalizedUser.roles?.[0] || 'USER');
  }, []);

  /**
   * Logout - Clears auth state and storage
   */
  const logout = useCallback(() => {
    setAuthState({
      isLoggedIn: false,
      user: null,
      tokens: {
        accessToken: null,
        refreshToken: null,
      },
      authReady: true,
    });

    // Clear localStorage
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    localStorage.removeItem('role');
  }, []);

  /**
   * Get current access token
   * @returns {string|null} The access token or null
   */
  const getToken = useCallback(() => {
    return authState.tokens.accessToken;
  }, [authState.tokens.accessToken]);

  /**
   * Initialize auth state from localStorage on mount
   * Call this in useEffect after provider wraps app
   */
  const initializeFromStorage = useCallback(() => {
    // Auth is now hydrated synchronously in the initial state to prevent
    // role-gated pages from making a public request first.
  }, []);

  const value = {
    ...authState,
    login,
    logout,
    getToken,
    initializeFromStorage,
    // Role-based helper methods
    hasDetailAccess: () => hasDetailAccess(authState.user?.role),
    canEditProperty: () => canEditProperty(authState.user?.role),
    canDeleteProperty: () => canDeleteProperty(authState.user?.role),
    canCreateProperty: () => canCreateProperty(authState.user?.role),
    getRolePermissions: () => getRolePermissions(authState.user?.role),
    isAdmin: () => isAdmin(authState.user?.role),
    isAgent: () => isAgent(authState.user?.role),
    isRegisteredUser: () => isRegisteredUser(authState.user?.role),
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

