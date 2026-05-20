import axiosInstance from '../../../shared/utils/api';
import { API_ENDPOINTS } from '../../../shared/utils/constants';

// ========== HELPERS ==========

/**
 * Extract response data ensuring consistent format
 */
const extractResponseData = (response) => {
  return Array.isArray(response.data) ? response.data : response.data.data || [];
};

const extractErrorMessage = (error, fallback) => {
  const responseError = error.response?.data?.error;
  if (typeof responseError === 'string') return responseError;
  if (responseError?.message) return responseError.message;
  if (error.response?.data?.message) return error.response.data.message;
  if (error.message) return error.message;
  return fallback;
};

const propertyListCache = new Map();
const propertyListRequests = new Map();

const normalizeRole = (role) => {
  const normalized = String(role || 'PUBLIC').trim().toUpperCase();

  if (normalized === 'ADMIN') return 'ADMIN';
  if (normalized === 'AGENT') return 'AGENT';
  if (normalized === 'USER' || normalized === 'REGISTERED_USER' || normalized === 'REGISTERED_USEER') {
    return 'REGISTERED_USER';
  }

  return 'PUBLIC';
};

/**
 * Get the endpoint base object for a given role
 */
const getEndpointForRole = (role) => {
  const normalizedRole = normalizeRole(role);
  const roleMap = {
    'ADMIN': API_ENDPOINTS.PROPERTIES_ADMIN,
    'AGENT': API_ENDPOINTS.PROPERTIES_AGENT,
    'REGISTERED_USER': API_ENDPOINTS.PROPERTIES_USER,
    'PUBLIC': API_ENDPOINTS.PROPERTIES_PUBLIC,
  };
  return roleMap[normalizedRole] || API_ENDPOINTS.PROPERTIES_PUBLIC;
};

/**
 * Generic method to search properties by role
 */
const searchPropertiesByRole = async (role, params = {}) => {
  const normalizedRole = normalizeRole(role);
  try {
    const endpoint = getEndpointForRole(normalizedRole);
    const searchParams = { ...(params || {}) };
    delete searchParams.sortMode;
    const response = await axiosInstance.get(endpoint.SEARCH, { params: searchParams });
    return extractResponseData(response);
  } catch (error) {
    throw new Error(extractErrorMessage(error, `Failed to search ${normalizedRole.toLowerCase()} properties`));
  }
};

/**
 * Generic method to get all properties by role
 */
const getAllPropertiesByRole = async (role) => {
  const normalizedRole = normalizeRole(role);
  const cacheKey = normalizedRole;

  if (propertyListCache.has(cacheKey)) {
    return propertyListCache.get(cacheKey);
  }

  if (propertyListRequests.has(cacheKey)) {
    return propertyListRequests.get(cacheKey);
  }

  try {
    const endpoint = getEndpointForRole(normalizedRole);
    const request = axiosInstance.get(endpoint.ALL)
      .then((response) => {
        const data = extractResponseData(response);
        propertyListCache.set(cacheKey, data);
        return data;
      })
      .catch((error) => {
        throw new Error(extractErrorMessage(error, `Failed to fetch ${normalizedRole.toLowerCase()} properties`));
      })
      .finally(() => {
        propertyListRequests.delete(cacheKey);
      });

    propertyListRequests.set(cacheKey, request);
    return request;
  } catch (error) {
    throw new Error(extractErrorMessage(error, `Failed to fetch ${normalizedRole.toLowerCase()} properties`));
  }
};

export const invalidatePropertyListCache = (role = null) => {
  if (role) {
    propertyListCache.delete(normalizeRole(role));
    return;
  }
  propertyListCache.clear();
};

/**
 * Generic method to get property details by role
 */
const getPropertyDetailsByRole = async (role, id) => {
  try {
    const normalizedRole = normalizeRole(role);
    const endpoint = getEndpointForRole(normalizedRole);
    // Handle different endpoint methods (DETAILS vs BY_ID)
    const detailsEndpoint = typeof endpoint.DETAILS === 'function' ? endpoint.DETAILS(id) : endpoint.BY_ID(id);
    const response = await axiosInstance.get(detailsEndpoint);
    return response.data.data || response.data;
  } catch (error) {
    throw new Error(extractErrorMessage(error, 'Failed to fetch property details'));
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
  const finalRole = normalizeRole(role);
  return searchPropertiesByRole(finalRole, params);
};

/**
 * Generic get all properties
 * Internally routes to appropriate role-based endpoint
 * 
 * @param {string} role - User role (ADMIN, AGENT, USER, PUBLIC)
 */
export const getProperties = async (role) => {
  const finalRole = normalizeRole(role);
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
  const finalRole = normalizeRole(role);
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
 * Get property details (registered user view)
 * Accessible to: Authenticated users (non-admin, non-agent)
 */
export const getUserPropertyDetails = async (id) => {
  return getPropertyDetailsByRole('REGISTERED_USER', id);
};

/**
 * Get property details (agent view - full details)
 * Accessible to: Agents
 */
export const getAgentPropertyDetails = async (id) => {
  return getPropertyDetailsByRole('AGENT', id);
};

/**
 * Get property details (admin view - full details)
 * Accessible to: Admins
 */
export const getAdminPropertyById = async (id) => {
  return getPropertyDetailsByRole('ADMIN', id);
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
    invalidatePropertyListCache();
    return response.data.data || response.data;
  } catch (error) {
    throw new Error(extractErrorMessage(error, 'Failed to create property'));
  }
};

export const uploadPropertyPhoto = async (file, displayOrder = 0) => {
  try {
    // Validate file size (max 5MB)
    const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    if (file.size > MAX_FILE_SIZE) {
      throw new Error(`File size exceeds maximum allowed size of 5MB. Your file is ${(file.size / (1024 * 1024)).toFixed(2)}MB`);
    }

    const formData = new FormData();
    formData.append('photo', file);
    formData.append('displayOrder', String(displayOrder));
    
    const response = await axiosInstance.post(
      API_ENDPOINTS.PROPERTIES_ADMIN.PHOTO_UPLOAD,
      formData
    );

    return response.data.data || response.data;
  } catch (error) {
    throw new Error(extractErrorMessage(error, 'Failed to upload property photo'));
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
    invalidatePropertyListCache();
    return response.data.data || response.data;
  } catch (error) {
    throw new Error(extractErrorMessage(error, 'Failed to update property'));
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
    invalidatePropertyListCache();
    return response.data;
  } catch (error) {
    throw error.response?.data?.error || 'Failed to delete property';
  }
};

export const getAmenities = async (defaultsOnly = false) => {
  try {
    const response = await axiosInstance.get(API_ENDPOINTS.PROPERTIES_ADMIN.AMENITIES, {
      params: { defaultsOnly },
    });
    return extractResponseData(response);
  } catch (error) {
    throw error.response?.data?.error || 'Failed to fetch amenities';
  }
};

// ========== UNIT MANAGEMENT (ADMIN ONLY) ==========

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
