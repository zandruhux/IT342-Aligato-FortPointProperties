import { useState, useCallback } from 'react';
import { useAuthContext } from '../../../shared/context/useAuthContext';
import { hasDetailAccess } from '../../../shared/utils/propertyHelpers';

/**
 * usePropertyDetailAccess Hook
 * Manages property detail modal state and role-based access control
 */
export const usePropertyDetailAccess = () => {
  const { user } = useAuthContext();
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [selectedProperty, setSelectedProperty] = useState(null);

  /**
   * Check if current user can access property details
   */
  const canAccess = useCallback(() => {
    return hasDetailAccess(user?.role);
  }, [user?.role]);

  /**
   * Open detail modal with property
   */
  const openDetailModal = useCallback(
    (property) => {
      if (!canAccess()) {
        console.warn('User does not have access to property details');
        return false;
      }
      setSelectedProperty(property);
      setIsDetailModalOpen(true);
      return true;
    },
    [canAccess]
  );

  /**
   * Close detail modal
   */
  const closeDetailModal = useCallback(() => {
    setIsDetailModalOpen(false);
    setSelectedProperty(null);
  }, []);

  /**
   * Handle View Details button click
   */
  const handleViewDetails = useCallback(
    (property) => {
      if (!canAccess()) {
        console.warn('User does not have access to property details');
        return;
      }
      openDetailModal(property);
    },
    [canAccess, openDetailModal]
  );

  return {
    isDetailModalOpen,
    selectedProperty,
    canAccess,
    openDetailModal,
    closeDetailModal,
    handleViewDetails,
  };
};
