import React from 'react';
import UserListItem from './UserListItem';

export default function UserList({ users, loading, onSelectUser }) {
  if (loading && users.length === 0) {
    return (
      <div className="bg-white border border-gray-200 rounded-lg p-10 text-center">
        <p className="text-gray-700 font-semibold">Loading users...</p>
      </div>
    );
  }

  if (!users.length) {
    return (
      <div className="bg-white border border-gray-200 rounded-lg p-10 text-center">
        <p className="text-gray-700 font-semibold">No users found.</p>
      </div>
    );
  }

  return (
    <div className="space-y-3 max-h-[calc(100vh-260px)] overflow-y-auto pr-2">
      {users.map((user) => (
        <UserListItem key={user.id} user={user} onClick={onSelectUser} />
      ))}
    </div>
  );
}
