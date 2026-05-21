import React, { useState } from 'react';
import { FiPlus, FiSearch, FiUsers, FiX } from 'react-icons/fi';
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
    searchKeyword,
    loading,
    error,
    changeRoleFilter,
    changeSearchKeyword,
    applySearch,
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

          <div className="mb-5 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <form
              className="flex w-full flex-col gap-2 sm:max-w-sm sm:flex-row"
              onSubmit={(event) => {
                event.preventDefault();
                applySearch();
              }}
            >
              <div className="relative w-full">
                <FiSearch className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={18} />
                <input
                  type="text"
                  value={searchKeyword}
                  onChange={(event) => changeSearchKeyword(event.target.value)}
                  placeholder="Search by first or last name"
                  aria-label="Search users by first or last name"
                  className="w-full rounded-lg border border-gray-200 bg-white py-2 pl-10 pr-10 text-sm font-semibold text-gray-900 outline-none transition placeholder:text-gray-400 focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
                />
                {searchKeyword && (
                  <button
                    type="button"
                    onClick={() => changeSearchKeyword('')}
                    aria-label="Clear user search"
                    className="absolute right-3 top-1/2 inline-flex h-5 w-5 -translate-y-1/2 items-center justify-center rounded-full text-gray-400 transition hover:bg-gray-100 hover:text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-100"
                  >
                    <FiX size={14} />
                  </button>
                )}
              </div>
              <button
                type="submit"
                disabled={loading}
                className="inline-flex items-center justify-center gap-2 px-4 bg-blue-600 hover:bg-blue-700 disabled:bg-slate-300 text-white rounded-lg font-semibold transition-colors"
              >
                Search
              </button>
            </form>
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
