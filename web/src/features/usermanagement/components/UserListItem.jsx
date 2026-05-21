import React from 'react';
import UserAvatar from './UserAvatar';

export default function UserListItem({ user, onClick }) {
  return (
    <button
      type="button"
      onClick={() => onClick(user)}
      className="w-full bg-white border border-gray-200 rounded-lg p-4 text-left hover:border-blue-400 hover:shadow-sm transition"
    >
      <div className="flex items-center gap-4">
        <UserAvatar user={user} />
        <div className="min-w-0 flex-1">
          <p className="font-bold text-gray-900 truncate">{user.fullName || `${user.firstname} ${user.lastname}`}</p>
          <p className="text-sm text-gray-600 truncate">{user.email}</p>
        </div>
        <div className="text-right flex-shrink-0">
          <span className="inline-block px-3 py-1 bg-blue-50 text-blue-700 rounded-full text-xs font-bold">
            {user.role}
          </span>
          {user.status && <p className="text-xs text-green-700 font-semibold mt-2">{user.status}</p>}
        </div>
      </div>
    </button>
  );
}
