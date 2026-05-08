import { AgentSidebar } from '../../../shared/components/layout';

/**
 * AgentBulletin Component
 * Placeholder page for agent bulletin board
 */
export default function AgentBulletin() {
  return (
    <div className="flex h-full">
      {/* Sidebar */}
      <AgentSidebar />
      
      {/* Main Content */}
      <div className="flex-1 ml-56 bg-gray-50 py-12">
        <div className="max-w-6xl mx-auto px-4">
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-gray-900 mb-2">Bulletin Board</h1>
            <p className="text-gray-600 text-lg">Latest announcements and updates</p>
          </div>

          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
            <p className="text-gray-600">
              This is a placeholder page for the agent bulletin board. Implementation details can be added
              based on your requirements.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
