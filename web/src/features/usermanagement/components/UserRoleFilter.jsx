import React from 'react';

const filters = [
  { label: 'All Users', value: '' },
  { label: 'Registered Users', value: 'REGISTERED_USER' },
  { label: 'Admin Users', value: 'ADMIN' },
  { label: 'Agent Users', value: 'AGENT' },
];

export default function UserRoleFilter({ selectedRole, onChange, disabled }) {
  return (
    <div className="flex flex-wrap gap-2">
      {filters.map((filter) => {
        const active = selectedRole === filter.value;
        return (
          <button
            key={filter.label}
            type="button"
            onClick={() => onChange(filter.value)}
            disabled={disabled}
            className={`px-4 py-2 rounded-lg text-sm font-semibold border transition ${
              active
                ? 'bg-blue-600 text-white border-blue-600'
                : 'bg-white text-gray-700 border-gray-200 hover:border-blue-400'
            }`}
          >
            {filter.label}
          </button>
        );
      })}
    </div>
  );
}
