import { useState } from 'react';
import AdminSidebar from '../../features/admin/components/common/AdminSidebar';
import AdminPropertiesSection from '../../features/admin/components/Properties/AdminPropertiesSection';

export default function AdminPropertiesPage({ onLogout }) {
  return (
    <div className="flex min-h-screen bg-slate-50">
      {/* Sidebar */}
      <AdminSidebar onLogout={onLogout} />

      {/* Main Content */}
      <div className="ml-64 flex-1 flex flex-col">
        {/* Header */}
        <header className="bg-gradient-to-r from-blue-600 to-blue-700 text-white shadow-lg sticky top-0 z-40">
          <div className="px-8 py-6">
            <h1 className="text-2xl font-bold">Admin Dashboard</h1>
            <p className="text-blue-100 text-sm mt-1">Manage all property listings in the system</p>
          </div>
        </header>

        {/* Content */}
        <main className="flex-1 overflow-auto">
          <div className="p-8">
            <AdminPropertiesSection />
          </div>
        </main>
      </div>
    </div>
  );
}
