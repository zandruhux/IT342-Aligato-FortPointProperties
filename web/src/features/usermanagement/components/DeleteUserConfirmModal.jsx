import React, { useState } from 'react';
import Modal from '../../../shared/components/ui/Modal';
import Button from '../../../shared/components/ui/Button';

export default function DeleteUserConfirmModal({ isOpen, user, onClose, onDelete, loading }) {
  const [error, setError] = useState('');

  const handleDelete = async () => {
    setError('');
    try {
      await onDelete(user.id);
      onClose();
    } catch (err) {
      setError(err?.message || 'Unable to remove user.');
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Remove User" size="md">
      <div className="space-y-4">
        <p className="text-gray-700">
          Remove <span className="font-bold">{user?.fullName || user?.email}</span> from the system?
        </p>
        {error && <p className="text-sm text-red-600 font-semibold">{error}</p>}
        <div className="flex justify-end gap-2">
          <Button type="button" variant="secondary" onClick={onClose}>Cancel</Button>
          <Button type="button" variant="danger" loading={loading} onClick={handleDelete}>Remove</Button>
        </div>
      </div>
    </Modal>
  );
}
