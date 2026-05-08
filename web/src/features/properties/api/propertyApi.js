import axiosInstance from '../../../shared/utils/api';
import { API_ENDPOINTS } from '../../../shared/utils/constants';

// ========== HELPERS ==========

/**
 * Extract response data ensuring consistent format
 */
const extractResponseData = (response) => {
  return Array.isArray(response.data) ? response.data : response.data.data || [];
};

/**
 * Get the endpoint base object for a given role
 */
const getEndpointForRole = (role) => {
  const roleMap = {
    'ADMIN': API_ENDPOINTS.PROPERTIES_ADMIN,
    'AGENT': API_ENDPOINTS.PROPERTIES_AGENT,
    'USER': API_ENDPOINTS.PROPERTIES_USER,
    'registered_user': API_ENDPOINTS.PROPERTIES_USER, // Backend role name
    'PUBLIC': API_ENDPOINTS.PROPERTIES_PUBLIC,
  };
  return roleMap[role] || API_ENDPOINTS.PROPERTIES_PUBLIC;
};

/**
 * Generic method to search properties by role
 */
const searchPropertiesByRole = async (role, params = {}) => {
  try {
    const endpoint = getEndpointForRole(role);
    const response = await axiosInstance.get(endpoint.SEARCH, { params });
    console.log(`[propertyApi] searchPropertiesByRole role=${role} params=`, params, 'response:', response.data);
    return extractResponseData(response);
  } catch (error) {
    throw error.response?.data?.error || `Failed to search ${role.toLowerCase()} properties`;
  }
};

/**
 * Generic method to get all properties by role
 */
const getAllPropertiesByRole = async (role) => {
  try {
    const endpoint = getEndpointForRole(role);
    const response = await axiosInstance.get(endpoint.ALL);
    console.log(`[propertyApi] getAllPropertiesByRole role=${role} response:`, response.data);
    return extractResponseData(response);
  } catch (error) {
    throw error.response?.data?.error || `Failed to fetch ${role.toLowerCase()} properties`;
  }
};

/**
 * Generic method to get property details by role
 */
const getPropertyDetailsByRole = async (role, id) => {
  try {
    const endpoint = getEndpointForRole(role);
    // Handle different endpoint methods (DETAILS vs BY_ID)
    const detailsEndpoint = typeof endpoint.DETAILS === 'function' ? endpoint.DETAILS(id) : endpoint.BY_ID(id);
    const response = await axiosInstance.get(detailsEndpoint);
    console.log(`[propertyApi] getPropertyDetailsByRole role=${role} id=${id} response:`, response.data);
    return response.data.data || response.data;
  } catch (error) {
    throw error.response?.data?.error || `Failed to fetch property details`;
  }
};

// ========== GENERIC ROLE-AGNOSTIC METHODS ==========

/**
 * Generic search for properties
 * Internally routes to appropriate role-based endpoint
 * Consolidates all search scenarios (by name, location, developer, price)
 * 
 * @param {string} role - User role (ADMIN, AGENT, USER, PUBLIC)
 * @param {object} params - Search parameters { name, location, developer, minPrice, maxPrice }
 */
export const searchProperties = async (role, params = {}) => {
  const finalRole = role || 'PUBLIC';
  return searchPropertiesByRole(finalRole, params);
};

/**
 * Generic get all properties
 * Internally routes to appropriate role-based endpoint
 * 
 * @param {string} role - User role (ADMIN, AGENT, USER, PUBLIC)
 */
export const getProperties = async (role) => {
  const finalRole = role || 'PUBLIC';
  return getAllPropertiesByRole(finalRole);
};

/**
 * Generic get property details
 * Internally routes to appropriate role-based endpoint
 * 
 * @param {string} role - User role (ADMIN, AGENT, USER, PUBLIC)
 * @param {number} id - Property ID
 */
export const getPropertyDetails = async (role, id) => {
  const finalRole = role || 'PUBLIC';
  return getPropertyDetailsByRole(finalRole, id);
};

// ========== PUBLIC DATA RETRIEVAL ==========

/**
 * Get all properties (public - limited fields)
 * Accessible to: Unauthenticated users
 */
export const getPublicProperties = async () => {
  return getAllPropertiesByRole('PUBLIC');
};

/**
 * Get featured properties (public - limited fields)
 * Accessible to: Unauthenticated users
 */
export const getPublicFeaturedProperties = async (limit = 4) => {
  const properties = await getPublicProperties();
  return properties.slice(0, limit);
};

/**
 * Combined search for public properties using optional filters
 */
export const searchPublicProperties = async (params = {}) => {
  return searchPropertiesByRole('PUBLIC', params);
};


// ========== REGISTERED USER DATA RETRIEVAL & SEARCH ==========

/**
 * Get all properties (registered user - limited fields)
 * Accessible to: Authenticated users (non-admin, non-agent)
 */
export const getUserProperties = async () => {
  return getAllPropertiesByRole('registered_user');
};

/**
 * Get property details (registered user view)
 * Accessible to: Authenticated users (non-admin, non-agent)
 */
export const getUserPropertyDetails = async (id) => {
  return getPropertyDetailsByRole('registered_user', id);
};

/**
 * Search user properties by name
 * Accessible to: Authenticated users (non-admin, non-agent)
 */
export const searchUserPropertyByName = async (name) => {
  return searchPropertiesByRole('registered_user', { name });
};

/**
 * Search user properties by location
 * Accessible to: Authenticated users (non-admin, non-agent)
 */
export const searchUserPropertyByLocation = async (location) => {
  return searchPropertiesByRole('registered_user', { location });
};

/**
 * Combined search for user properties using optional filters
 */
export const searchUserProperties = async (params = {}) => {
  return searchPropertiesByRole('registered_user', params);
};



// ========== AGENT DATA RETRIEVAL & SEARCH ==========

/**
 * Get all properties (agent view - full details)
 * Accessible to: Agents
 */
export const getAgentAllProperties = async () => {
  return getAllPropertiesByRole('AGENT');
};

/**
 * Get property details (agent view - full details)
 * Accessible to: Agents
 */
export const getAgentPropertyDetails = async (id) => {
  return getPropertyDetailsByRole('AGENT', id);
};

/**
 * Search agent properties by name
 * Accessible to: Agents
 */
export const searchAgentPropertyByName = async (name) => {
  return searchPropertiesByRole('AGENT', { name });
};

/**
 * Search agent properties by location
 * Accessible to: Agents
 */
export const searchAgentPropertyByLocation = async (location) => {
  return searchPropertiesByRole('AGENT', { location });
};

/**
 * Search agent properties by developer
 * Accessible to: Agents
 */
export const searchAgentPropertyByDeveloper = async (developerName) => {
  return searchPropertiesByRole('AGENT', { developer: developerName });
};

/**
 * Combined search for agent properties using optional filters
 */
export const searchAgentProperties = async (params = {}) => {
  return searchPropertiesByRole('AGENT', params);
};



// ========== ADMIN DATA RETRIEVAL, SEARCH & CRUD ==========

/**
 * Get all properties (admin view - full details)
 * Accessible to: Admins
 */
export const getAdminAllProperties = async () => {
  return getAllPropertiesByRole('ADMIN');
};

/**
 * Get property details (admin view - full details)
 * Accessible to: Admins
 */
export const getAdminPropertyById = async (id) => {
  return getPropertyDetailsByRole('ADMIN', id);
};

/**
 * Search admin properties by name
 * Accessible to: Admins
 */
export const searchAdminPropertyByName = async (name) => {
  return searchPropertiesByRole('ADMIN', { name });
};

/**
 * Search admin properties by location
 * Accessible to: Admins
 */
export const searchAdminPropertyByLocation = async (location) => {
  return searchPropertiesByRole('ADMIN', { location });
};

/**
 * Search admin properties by developer
 * Accessible to: Admins
 */
export const searchAdminPropertyByDeveloper = async (developerName) => {
  return searchPropertiesByRole('ADMIN', { developer: developerName });
};

/**
 * Combined search for admin properties using optional filters
 */
export const searchAdminProperties = async (params = {}) => {
  return searchPropertiesByRole('ADMIN', params);
};

/**
 * Create a new property
 * Accessible to: Admins
 */
export const createProperty = async (propertyData) => {
  try {
    const response = await axiosInstance.post(
      API_ENDPOINTS.PROPERTIES_ADMIN.CREATE,
      propertyData
    );
    return response.data.data || response.data;
  } catch (error) {
    throw error.response?.data?.error || 'Failed to create property';
  }
};

/**
 * Update an existing property
 * Accessible to: Admins
 */
export const updateProperty = async (id, propertyData) => {
  try {
    const response = await axiosInstance.put(
      API_ENDPOINTS.PROPERTIES_ADMIN.UPDATE(id),
      propertyData
    );
    return response.data.data || response.data;
  } catch (error) {
    throw error.response?.data?.error || 'Failed to update property';
  }
};

/**
 * Delete a property
 * Accessible to: Admins
 */
export const deleteProperty = async (id) => {
  try {
    const response = await axiosInstance.delete(
      API_ENDPOINTS.PROPERTIES_ADMIN.DELETE(id)
    );
    return response.data;
  } catch (error) {
    throw error.response?.data?.error || 'Failed to delete property';
  }
};



// ========== FAVORITES MANAGEMENT ==========

/**
 * Get all favorited properties
 * Accessible to: Authenticated users
 */
export const getAllFavorites = async () => {
  try {
    const response = await axiosInstance.get(API_ENDPOINTS.FAVORITES.ALL);
    return extractResponseData(response);
  } catch (error) {
    throw error.response?.data?.error || 'Failed to fetch favorites';
  }
};

/**
 * Add property to favorites
 * Accessible to: Authenticated users
 */
export const addToFavorites = async (propertyId) => {
  try {
    const response = await axiosInstance.post(
      API_ENDPOINTS.FAVORITES.ADD(propertyId),
      {}
    );
    return response.data;
  } catch (error) {
    throw error.response?.data?.error || 'Failed to add to favorites';
  }
};

/**
 * Remove property from favorites
 * Accessible to: Authenticated users
 */
export const removeFromFavorites = async (propertyId) => {
  try {
    const response = await axiosInstance.delete(
      API_ENDPOINTS.FAVORITES.REMOVE(propertyId)
    );
    return response.data;
  } catch (error) {
    throw error.response?.data?.error || 'Failed to remove from favorites';
  }
};

/**
 * Check if a property is favorited
 * Accessible to: Authenticated users
 */
export const checkIfFavorited = async (propertyId) => {
  try {
    const response = await axiosInstance.get(
      API_ENDPOINTS.FAVORITES.CHECK(propertyId)
    );
    return response.data.data;
  } catch (error) {
    throw error.response?.data?.error || 'Failed to check favorite status';
  }
};

/**
 * Get count of favorited properties
 * Accessible to: Authenticated users
 */
export const getFavoriteCount = async () => {
  try {
    const response = await axiosInstance.get(API_ENDPOINTS.FAVORITES.COUNT);
    return response.data.data;
  } catch (error) {
    throw error.response?.data?.error || 'Failed to fetch favorite count';
  }
};



// ========== UNIT MANAGEMENT (ADMIN ONLY) ==========

/**
 * Get all units for a property
 * Accessible to: Admins
 */
export const getAdminPropertyUnits = async (propertyId) => {
  try {
    const response = await axiosInstance.get(
      API_ENDPOINTS.PROPERTIES_ADMIN.UNITS.ALL(propertyId)
    );
    return extractResponseData(response);
  } catch (error) {
    throw error.response?.data?.error || 'Failed to fetch property units';
  }
};

/**
 * Create a new unit for a property
 * Accessible to: Admins
 */
export const createPropertyUnit = async (propertyId, unitData) => {
  try {
    const response = await axiosInstance.post(
      API_ENDPOINTS.PROPERTIES_ADMIN.UNITS.CREATE(propertyId),
      unitData
    );
    return response.data.data || response.data;
  } catch (error) {
    throw error.response?.data?.error || 'Failed to create property unit';
  }
};

/**
 * Update a property unit
 * Accessible to: Admins
 */
export const updatePropertyUnit = async (propertyId, unitId, unitData) => {
  try {
    const response = await axiosInstance.put(
      API_ENDPOINTS.PROPERTIES_ADMIN.UNITS.UPDATE(propertyId, unitId),
      unitData
    );
    return response.data.data || response.data;
  } catch (error) {
    throw error.response?.data?.error || 'Failed to update property unit';
  }
};

/**
 * Delete a property unit
 * Accessible to: Admins
 */
export const deletePropertyUnit = async (propertyId, unitId) => {
  try {
    const response = await axiosInstance.delete(
      API_ENDPOINTS.PROPERTIES_ADMIN.UNITS.DELETE(propertyId, unitId)
    );
    return response.data;
  } catch (error) {
    throw error.response?.data?.error || 'Failed to delete property unit';
  }
};
