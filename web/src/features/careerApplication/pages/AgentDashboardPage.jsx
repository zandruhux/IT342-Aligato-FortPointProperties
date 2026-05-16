import React from 'react';
import { AgentSidebar } from '../../../shared/components/layout';

export default function AgentDashboardPage() {
  return (
    <div className="flex min-h-screen bg-gray-50">
      <AgentSidebar />
      <main className="ml-56 flex-1 py-12">
        <div className="mx-auto max-w-6xl px-4 sm:px-6 lg:px-8">
          <h1 className="text-4xl font-bold text-gray-900">Agent Dashboard</h1>
          <p className="mt-2 text-gray-600">Welcome to your agent workspace.</p>
        </div>
      </main>
    </div>
  );
}
