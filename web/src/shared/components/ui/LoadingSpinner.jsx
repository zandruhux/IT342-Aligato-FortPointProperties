import React from 'react';

/**
 * LoadingSpinner component - Simple loading indicator
 * @param {string} size - Spinner size: sm, md, lg
 * @param {string} text - Optional loading text
 */
export default function LoadingSpinner({ size = 'md', text = 'Loading...' }) {
  const sizeClasses = {
    sm: 'h-6 w-6',
    md: 'h-10 w-10',
    lg: 'h-14 w-14',
  };

  return (
    <div className="flex flex-col items-center justify-center gap-4 py-8">
      <div className={`${sizeClasses[size] || sizeClasses.md} border-4 border-gray-200 border-t-blue-600 rounded-full animate-spin`} />
      {text && <p className="text-gray-600 text-sm">{text}</p>}
    </div>
  );
}
