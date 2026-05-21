import React from 'react';
import Modal from '../../../shared/components/ui/Modal';
import Button from '../../../shared/components/ui/Button';
import UserAvatar from './UserAvatar';

const getDisplayRole = (role) => {
  if (role === 'registered_user' || role === 'REGISTERED_USER') {
    return 'USER';
  }
  return role || 'USER';
};

export default function UserDetailsModal({ isOpen, user, onClose, onEditRole, onDelete }) {
  if (!user) return null;

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="User Details" size="xl">
      <div className="space-y-6">
        <div className="flex items-center gap-4">
          <UserAvatar user={user} size="lg" />
          <div>
            <h3 className="text-xl font-bold text-gray-900">{user.fullName || `${user.firstname} ${user.lastname}`.trim()}</h3>
            <p className="text-gray-600">{user.email}</p>
            <span className="inline-block mt-2 px-3 py-1 bg-blue-50 text-blue-700 rounded-full text-xs font-bold">
              {getDisplayRole(user.role)}
            </span>
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-sm">
          <Detail label="Status" value={user.status || 'Active'} />
          <Detail label="Phone Number" value={user.phoneNumber || 'null'} />
        </div>

        <div className="flex flex-col gap-2 border-t border-gray-200 pt-4 sm:flex-row sm:justify-end">
          <Button type="button" variant="secondary" onClick={onClose} className="sm:min-w-[120px]">Close</Button>
          <Button type="button" variant="outline" onClick={() => onEditRole(user)} className="sm:min-w-[140px]">Change Role</Button>
          <Button type="button" variant="danger" onClick={() => onDelete(user)} className="sm:min-w-[120px]">Remove</Button>
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
