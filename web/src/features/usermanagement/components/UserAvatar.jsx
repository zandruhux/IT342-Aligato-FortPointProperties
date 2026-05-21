import React from 'react';

const getInitials = (user) => {
  const name = user?.fullName || `${user?.firstname || ''} ${user?.lastname || ''}`.trim();
  return name
    .split(' ')
    .filter(Boolean)
    .map((part) => part[0])
    .join('')
    .toUpperCase() || 'U';
};

export default function UserAvatar({ user, size = 'md' }) {
  const sizeClass = size === 'lg' ? 'w-20 h-20 text-2xl' : 'w-12 h-12 text-sm';

  if (user?.profileImageUrl) {
    return (
      <img
        src={user.profileImageUrl}
        alt={`${user.fullName || user.email} profile`}
        className={`${sizeClass} rounded-full object-cover bg-gray-100`}
      />
    );
  }

  return (
    <div className={`${sizeClass} rounded-full bg-blue-600 text-white flex items-center justify-center font-bold`}>
      {getInitials(user)}
    </div>
  );
}
