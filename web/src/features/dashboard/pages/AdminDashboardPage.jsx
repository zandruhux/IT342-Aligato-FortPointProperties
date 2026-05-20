import { AdminSidebar } from '../../../shared/components/layout';

export default function AdminDashboardPage() {
  return (
    <div className="flex min-h-screen bg-gray-50">
      <AdminSidebar />
      <main className="ml-64 flex-1 py-12">
        <div className="mx-auto max-w-6xl px-4 sm:px-6 lg:px-8">
          <h1 className="text-4xl font-bold text-gray-900">Admin Dashboard</h1>
          <p className="mt-2 text-gray-600">Dashboard content will be added here soon.</p>
        </div>
      </main>
    </div>
  );
}
