import React from 'react';

const initials = (name) => (name || 'U')
  .split(' ')
  .filter(Boolean)
  .map((part) => part[0])
  .join('')
  .slice(0, 2)
  .toUpperCase();

export default function ConversationAvatar({ src, name, size = 'md' }) {
  const sizeClass = size === 'sm' ? 'h-8 w-8 text-xs' : size === 'lg' ? 'h-12 w-12 text-base' : 'h-10 w-10 text-sm';

  if (src) {
    return (
      <img
        src={src}
        alt={`${name || 'User'} profile`}
        className={`${sizeClass} flex-shrink-0 rounded-full object-cover bg-gray-100`}
      />
    );
  }

  return (
    <div className={`${sizeClass} flex-shrink-0 rounded-full bg-blue-600 text-white flex items-center justify-center font-bold`}>
      {initials(name)}
    </div>
  );
}
