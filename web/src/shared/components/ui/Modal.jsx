import React from 'react';
import { FiX } from 'react-icons/fi';

/**
 * Modal component - Generic reusable modal
 * @param {boolean} isOpen - Whether modal is open
 * @param {function} onClose - Callback when modal closes
 * @param {string} title - Modal title
 * @param {React.ReactNode} children - Modal content
 * @param {string} size - Modal size: sm, md, lg, xl
 */
export default function Modal({ isOpen, onClose, title, children, size = 'md' }) {
  if (!isOpen) return null;

  const sizeClasses = {
    sm: 'w-full sm:max-w-sm',
    md: 'w-full sm:max-w-md',
    lg: 'w-full sm:max-w-lg',
    xl: 'w-full sm:max-w-xl',
  };

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className={`${sizeClasses[size] || sizeClasses.md} bg-white rounded-lg shadow-xl max-h-96 overflow-hidden flex flex-col`}>
        {/* Header */}
        <div className="flex items-center justify-between border-b border-gray-200 px-6 py-4 flex-shrink-0">
          <h2 className="text-lg font-bold text-gray-900">{title}</h2>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition"
            aria-label="Close modal"
          >
            <FiX size={24} />
          </button>
        </div>

        {/* Body */}
        <div className="px-6 py-4 overflow-y-auto flex-grow">
          {children}
        </div>
      </div>
    </div>
  );
}
