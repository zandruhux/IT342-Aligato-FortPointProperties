import axios from 'axios';

// Replace with your actual backend URL
const API_BASE_URL = 'http://localhost:8080/'; 

const getAuthHeader = () => {
    const token = localStorage.getItem('accessToken');
    return {
        headers: {
            Authorization: `Bearer ${token}`,
        }
    };
};
export const property = {
  // Fetch all properties
  getAllProperties: async () => {
    const response = await axios.get(`${API_BASE_URL}properties`);
    return Array.isArray(response.data) ? response.data : response.data.data;  
  },
  
  // Optional: If your backend has a dedicated endpoint for featured limits
  getFeaturedProperties: async (limit = 4) => {
    const response = await axios.get(`${API_BASE_URL}properties`);
    const propertiesArray = Array.isArray(response.data) ? response.data : response.data.data;
    return propertiesArray.slice(0, limit);
  },

  getUserPropertyDetails: async (id) => {
    const response = await axios.get(`${API_BASE_URL}user/properties/${id}/advanced`, getAuthHeader());
    return Array.isArray(response.data) ? response.data : response.data.data || [];
  },

  getAgentPropertyDetails: async (id) => {
    const response = await axios.get(`${API_BASE_URL}agent/properties/${id}/advanced`, getAuthHeader());
    return Array.isArray(response.data) ? response.data : response.data.data || [];
  },

  // PUBLIC USER SEARCH
  searchPublicByLocation: async (location) => {
    const response = await axios.get(`${API_BASE_URL}properties/search/location`, {
      params: { location }
    });
    return Array.isArray(response.data) ? response.data : response.data.data || [];
  },

  // REGISTERED USER SEARCH
  searchUserByName: async (name) => {
    const response = await axios.get(`${API_BASE_URL}user/properties/search/name`, {
      params: { name },
      ...getAuthHeader()
    });
    return Array.isArray(response.data) ? response.data : response.data.data || [];
  },

  searchUserByLocation: async (location) => {
    const response = await axios.get(`${API_BASE_URL}user/properties/search/location`, {
      params: { location },
      ...getAuthHeader()
    });
    return Array.isArray(response.data) ? response.data : response.data.data || [];
  },

  // AGENT SEARCH
  searchAgentByName: async (name) => {
    const response = await axios.get(`${API_BASE_URL}agent/properties/search/name`, {
      params: { name },
      ...getAuthHeader()
    });
    return Array.isArray(response.data) ? response.data : response.data.data || [];
  },

  searchAgentByDeveloper: async (developerName) => {
    const response = await axios.get(`${API_BASE_URL}agent/properties/developer/${developerName}`, getAuthHeader());
    return Array.isArray(response.data) ? response.data : response.data.data || [];
  },

  // FAVORITES ENDPOINTS
  getAllFavorites: async () => {
    const response = await axios.get(`${API_BASE_URL}user/favorites`, getAuthHeader());
    return Array.isArray(response.data) ? response.data : response.data.data || [];
  },

  addToFavorites: async (propertyId) => {
    const response = await axios.post(`${API_BASE_URL}user/favorites/${propertyId}`, {}, getAuthHeader());
    return response.data;
  },

  removeFromFavorites: async (propertyId) => {
    const response = await axios.delete(`${API_BASE_URL}user/favorites/${propertyId}`, getAuthHeader());
    return response.data;
  },

  checkIfFavorited: async (propertyId) => {
    const response = await axios.get(`${API_BASE_URL}user/favorites/${propertyId}/check`, getAuthHeader());
    return response.data.data;
  },

  getFavoriteCount: async () => {
    const response = await axios.get(`${API_BASE_URL}user/favorites/count`, getAuthHeader());
    return response.data.data;
  }
};