import React, { createContext, useContext, useState, useCallback } from 'react';
import { hasDetailAccess, canEditProperty, canDeleteProperty, canCreateProperty } from '../utils/propertyHelpers';
import { getRolePermissions, isAdmin, isAgent, isRegisteredUser } from '../utils/roleRules';

const AuthContext = createContext(null);

/**
 * AuthProvider - Global authentication provider
 * Wraps the app to provide authentication state and methods
 */
export function AuthProvider({ children }) {
  const [authState, setAuthState] = useState({
    isLoggedIn: false,
    user: null,
    tokens: {
      accessToken: null,
      refreshToken: null,
    },
  });

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
      user: {
        id: normalizedUser.id,
        email: normalizedUser.email,
        firstname: normalizedUser.firstname,
        lastname: normalizedUser.lastname,
        role: normalizedUser.role || normalizedUser.roles?.[0] || 'USER',
      },
      tokens: {
        accessToken: normalizedTokens.accessToken,
        refreshToken: normalizedTokens.refreshToken,
      },
    });

    // Persist to localStorage
    localStorage.setItem('accessToken', normalizedTokens.accessToken);
    localStorage.setItem('refreshToken', normalizedTokens.refreshToken);
    localStorage.setItem('user', JSON.stringify(normalizedUser));
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
    const token = localStorage.getItem('accessToken');
    const userStr = localStorage.getItem('user');

    if (token && userStr) {
      try {
        const userData = JSON.parse(userStr);
        setAuthState({
          isLoggedIn: true,
          user: {
            id: userData.id,
            email: userData.email,
            firstname: userData.firstname,
            lastname: userData.lastname,
            role: userData.role || userData.roles?.[0] || 'USER',
          },
          tokens: {
            accessToken: token,
            refreshToken: localStorage.getItem('refreshToken'),
          },
        });
      } catch (error) {
        console.error('Failed to initialize auth from storage:', error);
      }
    }
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

/**
 * useAuthContext hook
 * Must be used within AuthProvider
 * @returns {Object} Auth context value
 */
export function useAuthContext() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuthContext must be used within AuthProvider');
  }
  return context;
}
