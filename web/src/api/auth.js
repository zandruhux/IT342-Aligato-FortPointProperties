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
      firstname: userData.firstname,
      lastname: userData.lastname,
      email: userData.email,
      password: userData.password,
      confirmPassword: userData.password,
    });

    console.log('Register Response:', JSON.stringify(response.data, null, 2));
    
    // Return the nested data structure
    if (response.data.success && response.data.data) {
      return response.data.data; // Returns { user, accessToken, refreshToken }
    }
    
    throw new Error('Unexpected response format');
  } catch (error) {
    console.error('Register Error:', JSON.stringify(error.response?.data, null, 2));
    // Handle error response with new structure
    if (error.response?.data?.error) {
      throw error.response.data;
    }
    throw { error: { message: error.message } };
  }
};

export const loginUser = async (credentials) => {
  try {
    const response = await authApi.post('/auth/login', {
      email: credentials.email,
      password: credentials.password,
    });

    console.log('Login Response:', JSON.stringify(response.data, null, 2));
    
    // Return the nested data structure
    if (response.data.success && response.data.data) {
      return response.data.data; // Returns { user, accessToken, refreshToken }
    }
    
    throw new Error('Unexpected response format');
  } catch (error) {
    console.error('Login Error:', JSON.stringify(error.response?.data, null, 2));
    // Handle error response with new structure
    if (error.response?.data?.error) {
      throw error.response.data;
    }
    throw { error: { message: error.message } };
  }
};

export default authApi;
