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

  filterAgentByListingType: async (listingType) => {
    const response = await axios.get(`${API_BASE_URL}agent/properties/filter/listing-type`, {
      params: { listingType },
      ...getAuthHeader()
    });
    return Array.isArray(response.data) ? response.data : response.data.data || [];
  },

  searchAgentByDeveloper: async (developerName) => {
    const response = await axios.get(`${API_BASE_URL}agent/properties/developer/${developerName}`, getAuthHeader());
    return Array.isArray(response.data) ? response.data : response.data.data || [];
  },

  getAllAgentProperties: async () => {
    const response = await axios.get(`${API_BASE_URL}agent/properties`, getAuthHeader());
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
  },

  // ADMIN ENDPOINTS
  getAllAdminProperties: async () => {
    const response = await axios.get(`${API_BASE_URL}admin/properties`, getAuthHeader());
    return Array.isArray(response.data) ? response.data : response.data.data || [];
  },

  getAdminPropertyById: async (id) => {
    const response = await axios.get(`${API_BASE_URL}admin/properties/${id}`, getAuthHeader());
    return response.data.data || response.data;
  },

  createProperty: async (propertyData) => {
    const response = await axios.post(`${API_BASE_URL}admin/properties`, propertyData, getAuthHeader());
    return response.data.data || response.data;
  },

  updateProperty: async (id, propertyData) => {
    const response = await axios.put(`${API_BASE_URL}admin/properties/${id}`, propertyData, getAuthHeader());
    return response.data.data || response.data;
  },

  deleteProperty: async (id) => {
    const response = await axios.delete(`${API_BASE_URL}admin/properties/${id}`, getAuthHeader());
    return response.data;
  },

  // ADMIN SEARCH ENDPOINTS
  searchPropertyByName: async (name) => {
    const response = await axios.get(`${API_BASE_URL}admin/properties/search/name`, {
      params: { name },
      ...getAuthHeader()
    });
    return Array.isArray(response.data) ? response.data : response.data.data || [];
  },

  searchPropertyByLocation: async (location) => {
    const response = await axios.get(`${API_BASE_URL}admin/properties/search/location`, {
      params: { location },
      ...getAuthHeader()
    });
    return Array.isArray(response.data) ? response.data : response.data.data || [];
  },

  searchPropertyByDeveloper: async (developerName) => {
    const response = await axios.get(`${API_BASE_URL}admin/properties/developer/${developerName}`, getAuthHeader());
    return Array.isArray(response.data) ? response.data : response.data.data || [];
  },

  // ========== UNIT MANAGEMENT ENDPOINTS ==========

  // Get all units for a property (Admin)
  getAdminPropertyUnits: async (propertyId) => {
    const response = await axios.get(`${API_BASE_URL}admin/properties/${propertyId}/units`, getAuthHeader());
    return Array.isArray(response.data) ? response.data : response.data.data || [];
  },

  // Create a new unit (Admin)
  createPropertyUnit: async (propertyId, unitData) => {
    const response = await axios.post(`${API_BASE_URL}admin/properties/${propertyId}/units`, unitData, getAuthHeader());
    return response.data.data || response.data;
  },

  // Update a unit (Admin)
  updatePropertyUnit: async (propertyId, unitId, unitData) => {
    const response = await axios.put(`${API_BASE_URL}admin/properties/${propertyId}/units/${unitId}`, unitData, getAuthHeader());
    return response.data.data || response.data;
  },

  // Delete a unit (Admin)
  deletePropertyUnit: async (propertyId, unitId) => {
    const response = await axios.delete(`${API_BASE_URL}admin/properties/${propertyId}/units/${unitId}`, getAuthHeader());
    return response.data;
  },


};