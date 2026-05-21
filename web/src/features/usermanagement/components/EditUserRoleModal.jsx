import React, { useEffect, useState } from 'react';
import Modal from '../../../shared/components/ui/Modal';
import Button from '../../../shared/components/ui/Button';

export default function EditUserRoleModal({ isOpen, user, onClose, onUpdateRole, loading }) {
  const [role, setRole] = useState(user?.role === 'registered_user' ? 'REGISTERED_USER' : user?.role || 'REGISTERED_USER');
  const [error, setError] = useState('');

  useEffect(() => {
    setRole(user?.role === 'registered_user' ? 'REGISTERED_USER' : user?.role || 'REGISTERED_USER');
    setError('');
  }, [user]);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    try {
      await onUpdateRole(user.id, role);
      onClose();
    } catch (err) {
      setError(err?.message || 'Unable to update role.');
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Change Role" size="md">
      <form onSubmit={handleSubmit} className="space-y-4">
        <select className="w-full border border-gray-300 rounded-lg px-3 py-2" value={role} onChange={(e) => setRole(e.target.value)}>
          <option value="REGISTERED_USER">Registered User</option>
          <option value="AGENT">Agent</option>
          <option value="ADMIN">Admin</option>
        </select>
        {error && <p className="text-sm text-red-600 font-semibold">{error}</p>}
        <div className="flex justify-end gap-2">
          <Button type="button" variant="secondary" onClick={onClose}>Cancel</Button>
          <Button type="submit" loading={loading}>Save Role</Button>
        </div>
      </form>
    </Modal>
  );
}
