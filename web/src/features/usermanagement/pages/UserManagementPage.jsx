import React, { useState } from 'react';
import { FiPlus, FiUsers } from 'react-icons/fi';
import { AdminSidebar } from '../../../shared/components/layout';
import Button from '../../../shared/components/ui/Button';
import { useUserManagement } from '../hooks/useUserManagement';
import UserRoleFilter from '../components/UserRoleFilter';
import UserList from '../components/UserList';
import UserDetailsModal from '../components/UserDetailsModal';
import AddUserModal from '../components/AddUserModal';
import EditUserRoleModal from '../components/EditUserRoleModal';
import DeleteUserConfirmModal from '../components/DeleteUserConfirmModal';

export default function UserManagementPage() {
  const {
    users,
    selectedRole,
    loading,
    error,
    changeRoleFilter,
    createUser,
    updateUserRole,
    deleteUser,
  } = useUserManagement();

  const [selectedUser, setSelectedUser] = useState(null);
  const [showAddModal, setShowAddModal] = useState(false);
  const [roleUser, setRoleUser] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);

  return (
    <div className="flex h-full">
      <AdminSidebar />
      <div className="flex-1 ml-64 bg-slate-50 py-10 min-h-screen">
        <div className="max-w-6xl mx-auto px-6">
          <div className="flex items-center justify-between mb-6">
            <div>
              <div className="flex items-center gap-3">
                <FiUsers className="text-blue-600" size={28} />
                <h1 className="text-3xl font-bold text-slate-900">User Management</h1>
              </div>
              <p className="text-slate-600 mt-1">Manage accounts, roles, and access.</p>
            </div>
            <Button type="button" onClick={() => setShowAddModal(true)} className="inline-flex items-center gap-2">
              <FiPlus size={18} />
              Add User
            </Button>
          </div>

          <div className="mb-5">
            <UserRoleFilter selectedRole={selectedRole} onChange={changeRoleFilter} disabled={loading} />
          </div>

          {error && (
            <div className="mb-4 bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 font-semibold">
              {error}
            </div>
          )}

          <UserList users={users} loading={loading} onSelectUser={setSelectedUser} />
        </div>
      </div>

      <UserDetailsModal
        isOpen={!!selectedUser}
        user={selectedUser}
        onClose={() => setSelectedUser(null)}
        onEditRole={(user) => {
          setRoleUser(user);
          setSelectedUser(null);
        }}
        onDelete={(user) => {
          setDeleteTarget(user);
          setSelectedUser(null);
        }}
      />
      <AddUserModal isOpen={showAddModal} onClose={() => setShowAddModal(false)} onCreate={createUser} loading={loading} />
      <EditUserRoleModal isOpen={!!roleUser} user={roleUser} onClose={() => setRoleUser(null)} onUpdateRole={updateUserRole} loading={loading} />
      <DeleteUserConfirmModal isOpen={!!deleteTarget} user={deleteTarget} onClose={() => setDeleteTarget(null)} onDelete={deleteUser} loading={loading} />
    </div>
  );
}
