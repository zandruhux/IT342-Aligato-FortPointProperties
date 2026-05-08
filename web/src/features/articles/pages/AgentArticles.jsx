import { AgentSidebar } from '../../../shared/components/layout';

/**
 * AgentArticles Component
 * Placeholder page for articles management
 */
export default function AgentArticles() {
  return (
    <div className="flex h-full">
      {/* Sidebar */}
      <AgentSidebar />
      
      {/* Main Content */}
      <div className="flex-1 ml-56 bg-gray-50 py-12">
        <div className="max-w-6xl mx-auto px-4">
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-gray-900 mb-2">Articles</h1>
            <p className="text-gray-600 text-lg">View and manage articles</p>
          </div>

          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
            <div className="mb-4">
              <svg className="w-16 h-16 mx-auto text-gray-400 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path>
              </svg>
            </div>
            <h2 className="text-2xl font-bold text-gray-900 mb-2">Coming Soon</h2>
            <p className="text-gray-600 mb-6">
              Articles feature is currently under development. Backend API endpoints need to be implemented
              first.
            </p>
            <p className="text-sm text-gray-500">TODO: Create backend endpoints for articles management</p>
          </div>
        </div>
      </div>
    </div>
  );
}
