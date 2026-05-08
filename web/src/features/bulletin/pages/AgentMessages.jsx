import { AgentSidebar } from '../../../shared/components/layout';

/**
 * AgentMessages Component
 * Placeholder page for agent messaging
 */
export default function AgentMessages() {
  return (
    <div className="flex h-full">
      {/* Sidebar */}
      <AgentSidebar />
      
      {/* Main Content */}
      <div className="flex-1 ml-56 bg-gray-50 py-12">
        <div className="max-w-6xl mx-auto px-4">
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-gray-900 mb-2">Messages</h1>
            <p className="text-gray-600 text-lg">Communication center for agents</p>
          </div>

          <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-12 text-center">
            <p className="text-gray-600">
              This is a placeholder page for agent messaging. Implementation details can be added based on
              your requirements.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
