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
  const [searchKeyword, setSearchKeyword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchUsers = useCallback(async (role = '', search = '') => {
    setLoading(true);
    setError('');
    try {
      const data = await userManagementApi.getUsers({ role, search });
      setUsers(data || []);
    } catch (err) {
      setError(err?.message || 'Failed to fetch users');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchUsers(selectedRole, searchKeyword);
  }, [fetchUsers, searchKeyword, selectedRole]);

  const changeRoleFilter = (role) => {
    setSelectedRole(role);
  };

  const changeSearchKeyword = (keyword) => {
    setSearchKeyword(keyword);
  };

  const applySearch = useCallback(async () => {
    await fetchUsers(selectedRole, searchKeyword);
  }, [fetchUsers, searchKeyword, selectedRole]);

  const createUser = async (data) => {
    setLoading(true);
    setError('');
    try {
      await userManagementApi.createUser(data);
      await fetchUsers(selectedRole, searchKeyword);
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
      await fetchUsers(selectedRole, searchKeyword);
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
    searchKeyword,
    loading,
    error,
    fetchUsers,
    changeRoleFilter,
    changeSearchKeyword,
    applySearch,
    createUser,
    updateUserRole,
    deleteUser,
  };
};
