import AdminSidebar from '../../features/admin/components/common/AdminSidebar';

export default function AdminSettingsPage({ onLogout }) {
  return (
    <div className="flex min-h-screen bg-slate-50">
      {/* Sidebar */}
      <AdminSidebar onLogout={onLogout} />

      {/* Main Content */}
      <div className="ml-64 flex-1 flex flex-col">
        {/* Header */}
        <header className="bg-gradient-to-r from-blue-600 to-blue-700 text-white shadow-lg sticky top-0 z-40">
          <div className="px-8 py-6">
            <h1 className="text-2xl font-bold">Settings</h1>
            <p className="text-blue-100 text-sm mt-1">Configure system settings and preferences</p>
          </div>
        </header>

        {/* Content */}
        <main className="flex-1 overflow-auto">
          <div className="p-8">
            <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-12 text-center">
              <h2 className="text-2xl font-bold text-slate-900 mb-2">Settings</h2>
              <p className="text-slate-600">System settings and configuration options coming soon.</p>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
