import { useState, useCallback } from 'react';
import { useAuthContext } from '../../../shared/context/useAuthContext';
import { hasDetailAccess } from '../../../shared/utils/propertyHelpers';

export const usePropertyDetailAccess = () => {
  const { user } = useAuthContext();
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [selectedProperty, setSelectedProperty] = useState(null);

  const canAccess = useCallback(() => {
    return hasDetailAccess(user?.role);
  }, [user?.role]);

  const openDetailModal = useCallback(
    (property) => {
      if (!canAccess()) {
        return false;
      }
      setSelectedProperty(property);
      setIsDetailModalOpen(true);
      return true;
    },
    [canAccess]
  );

  const closeDetailModal = useCallback(() => {
    setIsDetailModalOpen(false);
    setSelectedProperty(null);
  }, []);

  const handleViewDetails = useCallback(
    (property) => {
      if (!canAccess()) {
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
