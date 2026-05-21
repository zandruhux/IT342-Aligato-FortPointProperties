import React from 'react';
import Modal from '../../../shared/components/ui/Modal';
import Button from '../../../shared/components/ui/Button';
import UserAvatar from './UserAvatar';

const formatDate = (value) => {
  if (!value) return '-';
  return new Date(value).toLocaleString();
};

export default function UserDetailsModal({ isOpen, user, onClose, onEditRole, onDelete }) {
  if (!user) return null;

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="User Details" size="xl">
      <div className="space-y-6">
        <div className="flex items-center gap-4">
          <UserAvatar user={user} size="lg" />
          <div>
            <h3 className="text-xl font-bold text-gray-900">{user.fullName || `${user.firstname} ${user.lastname}`}</h3>
            <p className="text-gray-600">{user.email}</p>
            <span className="inline-block mt-2 px-3 py-1 bg-blue-50 text-blue-700 rounded-full text-xs font-bold">
              {user.role}
            </span>
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm">
          <Detail label="Status" value={user.status || 'Active'} />
          <Detail label="User ID" value={user.id} />
          <Detail label="Created" value={formatDate(user.createdAt)} />
          <Detail label="Updated" value={formatDate(user.updatedAt)} />
        </div>

        <div className="flex justify-end gap-2">
          <Button type="button" variant="secondary" onClick={onClose}>Close</Button>
          <Button type="button" variant="outline" onClick={() => onEditRole(user)}>Change Role</Button>
          <Button type="button" variant="danger" onClick={() => onDelete(user)}>Remove</Button>
        </div>
      </div>
    </Modal>
  );
}

function Detail({ label, value }) {
  return (
    <div>
      <p className="text-xs uppercase tracking-wide font-bold text-gray-500 mb-1">{label}</p>
      <p className="text-gray-900 break-words font-semibold">{value}</p>
    </div>
  );
}
