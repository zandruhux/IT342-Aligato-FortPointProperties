import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/v1';

const authApi = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const registerUser = async (userData) => {
  try {
    const response = await authApi.post('/auth/register', {
      firstname: userData.firstName,
      lastname: userData.lastName,
      email: userData.email,
      password: userData.password,
    });
    return response.data;
  } catch (error) {
    throw error.response?.data || error.message;
  }
};

export default authApi;
