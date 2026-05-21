import { useCallback, useEffect, useState } from 'react';
import * as userManagementApi from '../api/userManagementApi';

export const USER_ROLE_OPTIONS = [
  { value: 'REGISTERED_USER', label: 'Registered Users' },
  { value: 'ADMIN', label: 'Admin Users' },
  { value: 'AGENT', label: 'Agent Users' },
];

export const useUserManagement = () => {
  const [users, setUsers] = useState([]);
  const [selectedRole, setSelectedRole] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchUsers = useCallback(async (role = '') => {
    setLoading(true);
    setError('');
    try {
      const data = role
        ? await userManagementApi.getUsersByRole(role)
        : await userManagementApi.getAllUsers();
      setUsers(data || []);
    } catch (err) {
      setError(err?.message || 'Failed to fetch users');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchUsers('');
  }, [fetchUsers]);

  const changeRoleFilter = async (role) => {
    setSelectedRole(role);
    await fetchUsers(role);
  };

  const createUser = async (data) => {
    setLoading(true);
    setError('');
    try {
      await userManagementApi.createUser(data);
      await fetchUsers(selectedRole);
    } catch (err) {
      setError(err?.message || 'Failed to create user');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const updateUserRole = async (userId, role) => {
    setLoading(true);
    setError('');
    try {
      const updated = await userManagementApi.updateUserRole(userId, role);
      setUsers((current) => current.map((user) => (user.id === userId ? updated : user)));
      return updated;
    } catch (err) {
      setError(err?.message || 'Failed to update user role');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const deleteUser = async (userId) => {
    setLoading(true);
    setError('');
    try {
      await userManagementApi.deleteUser(userId);
      setUsers((current) => current.filter((user) => user.id !== userId));
    } catch (err) {
      setError(err?.message || 'Failed to delete user');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  return {
    users,
    selectedRole,
    loading,
    error,
    fetchUsers,
    changeRoleFilter,
    createUser,
    updateUserRole,
    deleteUser,
  };
};
