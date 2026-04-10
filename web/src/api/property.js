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
        return Array.isArray(response.data) ? response.data : response.data.data || [];},

    getAgentPropertyDetails: async (id) => {
        const response = await axios.get(`${API_BASE_URL}agent/properties/${id}/advanced`, getAuthHeader());
        return Array.isArray(response.data) ? response.data : response.data.data || [];
    }

};