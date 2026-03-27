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

export const loginWithGoogle = async (tokenResponse) => {
  try {
    // Validate tokenResponse
    if (!tokenResponse) {
      throw new Error('No token response from Google');
    }

    // Extract token - handle both access_token and credential (ID token)
    const token = tokenResponse.access_token || tokenResponse.credential;
    if (!token) {
      throw new Error('No access token received from Google');
    }

    // tokenResponse contains the access token from Google OAuth
    // Send it to the backend for verification and user creation/retrieval
    const response = await authApi.post('/auth/google/login', null, {
      params: {
        googleToken: token,
      },
    });

    console.log('Google Login Response:', JSON.stringify(response.data, null, 2));
    
    // Return the nested data structure
    if (response.data.success && response.data.data) {
      return response.data.data; // Returns { user, accessToken, refreshToken }
    }
    
    throw new Error('Unexpected response format');
  } catch (error) {
    console.error('Google Login Error Details:', error);
    
    // Handle different error types
    if (error.response?.data?.error) {
      console.error('Server Error:', JSON.stringify(error.response.data, null, 2));
      throw error.response.data;
    } else if (error.response?.data) {
      console.error('Response Error:', JSON.stringify(error.response.data, null, 2));
      throw { error: { message: error.response.data.message || 'Google login failed' } };
    } else if (error.response?.status) {
      throw { error: { message: `Server error: ${error.response.status}` } };
    }
    
    // Handle general errors
    throw { error: { message: error.message || 'Google login failed. Please try again.' } };
  }
};

export default authApi;
