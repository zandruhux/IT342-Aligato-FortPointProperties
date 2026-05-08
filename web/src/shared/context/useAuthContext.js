import { useContext } from 'react';
import { AuthContext } from './AuthContextBase';

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
