import axiosInstance from '../../../shared/utils/api';
import { API_ENDPOINTS } from '../../../shared/utils/constants';
import { normalizeApiError } from '../../../shared/utils/errors';

const unwrap = (response) => {
  if (response.data.success) {
    return response.data.data;
  }
  throw new Error('Unexpected response format');
};

const toError = (error, fallback) => {
  return normalizeApiError(error, fallback);
};

export const getUsers = async ({ role = '', search = '' } = {}) => {
  const params = {};
  const keyword = search.trim();

  if (role) {
    params.role = role;
  }
  if (keyword) {
    params.search = keyword;
  }

  try {
    return unwrap(await axiosInstance.get(API_ENDPOINTS.USER_MANAGEMENT.USERS, { params }));
  } catch (error) {
    throw toError(error, 'Failed to fetch users');
  }
};

export const getAllUsers = async () => getUsers();

export const getUsersByRole = async (role) => getUsers({ role });

export const createUser = async (data) => {
  try {
    return unwrap(await axiosInstance.post(API_ENDPOINTS.USER_MANAGEMENT.USERS, data));
  } catch (error) {
    throw toError(error, 'Failed to create user');
  }
};

export const updateUserRole = async (userId, role) => {
  try {
    return unwrap(await axiosInstance.put(API_ENDPOINTS.USER_MANAGEMENT.USER_ROLE(userId), { role }));
  } catch (error) {
    throw toError(error, 'Failed to update user role');
  }
};

export const deleteUser = async (userId) => {
  try {
    await axiosInstance.delete(API_ENDPOINTS.USER_MANAGEMENT.USER(userId));
  } catch (error) {
    throw toError(error, 'Failed to delete user');
  }
};
