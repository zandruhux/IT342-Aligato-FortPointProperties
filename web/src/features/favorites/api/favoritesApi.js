//import * as propertyApi from '../../properties/api/propertyApi';
//import axiosInstance from '../../../shared/api/axiosInstance';
import axiosInstance from '../../../shared/utils/api';
import { API_ENDPOINTS } from '../../../shared/utils/constants';

//HELPER FUNCTION
const extractResponseData = (response) => {
  return Array.isArray(response.data) ? response.data : response.data.data || [];
};

export const getAllFavorites = async () => {
  try {
    const response = await axiosInstance.get(API_ENDPOINTS.FAVORITES.ALL);
    console.log("Favorites: ", response.data);
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