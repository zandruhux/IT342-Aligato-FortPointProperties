import { AdminSidebar } from '../../../shared/components/layout';

/**
 * AdminArticles Component
 * Placeholder page for articles management
 */
export default function AdminArticles() {
  return (
    <div className="flex h-full">
      {/* Sidebar */}
      <AdminSidebar />
      
      {/* Main Content */}
      <div className="flex-1 ml-64 bg-slate-50 py-12">
        <div className="max-w-6xl mx-auto px-4">
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-slate-900 mb-2">Articles Management</h1>
            <p className="text-slate-600 text-lg">Manage and publish articles</p>
          </div>

          <div className="bg-white rounded-lg shadow-sm border border-slate-200 p-12 text-center">
            <div className="mb-4">
              <svg className="w-16 h-16 mx-auto text-slate-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
              </svg>
            </div>
            <h2 className="text-2xl font-bold text-slate-900 mb-2">Coming Soon</h2>
            <p className="text-slate-600 mb-6">
              Articles management feature is currently under development. Backend API endpoints need to be
              implemented first.
            </p>
            <p className="text-sm text-slate-500">TODO: Create backend endpoints for articles CRUD operations</p>
          </div>
        </div>
      </div>
    </div>
  );
}
