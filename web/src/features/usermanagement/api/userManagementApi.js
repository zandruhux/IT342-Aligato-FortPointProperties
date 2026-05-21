import axiosInstance from '../../../shared/utils/api';
import { API_ENDPOINTS } from '../../../shared/utils/constants';

const unwrap = (response) => {
  if (response.data.success) {
    return response.data.data;
  }
  throw new Error('Unexpected response format');
};

const toError = (error, fallback) => {
  return error.response?.data?.error || { message: fallback };
};

export const getAllUsers = async () => {
  try {
    return unwrap(await axiosInstance.get(API_ENDPOINTS.USER_MANAGEMENT.USERS));
  } catch (error) {
    throw toError(error, 'Failed to fetch users');
  }
};

export const getUsersByRole = async (role) => {
  try {
    return unwrap(await axiosInstance.get(API_ENDPOINTS.USER_MANAGEMENT.USERS, { params: { role } }));
  } catch (error) {
    throw toError(error, 'Failed to fetch users');
  }
};

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
