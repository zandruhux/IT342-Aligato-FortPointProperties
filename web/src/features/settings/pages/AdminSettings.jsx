import { AdminSidebar } from '../../../shared/components/layout';

/**
 * AdminSettings Component
 * Placeholder page for admin system settings
 */
export default function AdminSettings() {
  return (
    <div className="flex h-full">
      {/* Sidebar */}
      <AdminSidebar />
      
      {/* Main Content */}
      <div className="flex-1 ml-64 bg-slate-50 py-12">
        <div className="max-w-4xl mx-auto px-4">
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-slate-900 mb-2">System Settings</h1>
            <p className="text-slate-600 text-lg">Manage system configuration and preferences</p>
          </div>

          <div className="grid grid-cols-1 gap-6">
            <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-6">
              <h2 className="text-xl font-bold text-slate-900 mb-4">Settings Placeholder</h2>
              <p className="text-slate-600">
                This is a placeholder page for admin system settings. Implementation details can be added
                based on your system requirements.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
