/**
 * Property Helper Functions
 * Pure utility functions for property data transformations and validations
 */

/**
 * Format price in PHP currency
 * @param {number} price - Price value
 * @returns {string} Formatted price string (e.g., "₱1,234,567.89")
 */
export const formatPrice = (price) => {
  if (!price && price !== 0) return '₱0';
  return new Intl.NumberFormat('en-PH', {
    style: 'currency',
    currency: 'PHP',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(price);
};

/**
 * Format price range
 * @param {number} min - Minimum price
 * @param {number} max - Maximum price
 * @returns {string} Formatted range (e.g., "₱1M - ₱5M")
 */
export const formatPriceRange = (min, max) => {
  return `${formatPrice(min)} - ${formatPrice(max)}`;
};

/**
 * Check if user has access to property details based on role
 * @param {string} userRole - User role (ADMIN, AGENT, USER, null for public)
 * @returns {boolean} True if user can view property details
 */
export const hasDetailAccess = (userRole) => {
  if (!userRole) return false; // Public users (not logged in)
  // ADMIN, AGENT, registered users all have access
  return ['ADMIN', 'AGENT', 'registered_user', 'USER'].includes(userRole);
};

/**
 * Check if user can edit/delete property based on role
 * @param {string} userRole - User role
 * @returns {boolean} True if user can edit properties
 */
export const canEditProperty = (userRole) => {
  return ['ADMIN', 'AGENT'].includes(userRole);
};

/**
 * Check if user can delete property based on role
 * @param {string} userRole - User role
 * @returns {boolean} True if user can delete properties
 */
export const canDeleteProperty = (userRole) => {
  return userRole === 'ADMIN';
};

/**
 * Check if user can create property based on role
 * @param {string} userRole - User role
 * @returns {boolean} True if user can create properties
 */
export const canCreateProperty = (userRole) => {
  return userRole === 'ADMIN';
};

/**
 * Filter properties by listing type
 * @param {Array} properties - Array of property objects
 * @param {Array} listingTypes - Array of listing types to filter by (e.g., ["Pre-Selling", "RFO"])
 * @returns {Array} Filtered properties
 */
export const filterByListingType = (properties, listingTypes) => {
  if (!listingTypes || listingTypes.length === 0) return properties;

  return properties.filter((prop) => {
    const propTypes = prop.listingType ? prop.listingType.split(',').map((t) => t.trim()) : [];
    return listingTypes.some((type) => propTypes.includes(type));
  });
};

/**
 * Filter properties by price range
 * @param {Array} properties - Array of property objects
 * @param {number} minPrice - Minimum price filter
 * @param {number} maxPrice - Maximum price filter
 * @returns {Array} Filtered properties
 */
export const filterByPriceRange = (properties, minPrice, maxPrice) => {
  if (minPrice === null && maxPrice === null) return properties;

  return properties.filter((prop) => {
    const min = minPrice !== null ? minPrice : 0;
    const max = maxPrice !== null ? maxPrice : Infinity;
    // Check if property price range overlaps with filter range
    return prop.priceRangeMin <= max && prop.priceRangeMax >= min;
  });
};

/**
 * Get role-specific detail view permissions
 * @param {string} userRole - User role
 * @returns {Object} Object with permissions for each field
 */
export const getDetailViewPermissions = (userRole) => {
  const baseFields = {
    id: false,
    name: true,
    basicDescription: true,
    location: true,
    priceRangeMin: true,
    priceRangeMax: true,
    listingType: true,
    petFriendly: true,
    parkingAvailable: true,
    turnoverDate: true,
    amenities: true,
    createdAt: false,
    updatedAt: false,
    units: true,
  };

  // Admin and Agent see all fields
  if (['ADMIN', 'AGENT'].includes(userRole)) {
    return {
      ...baseFields,
      id: true,
      developer: true,
      keySellingPoints: true,
      brochurePdfUrl: true,
      inventoryLink: true,
      createdBy: true,
    };
  }

  // RegisteredUser sees base fields + some additional
  if (userRole === 'registered_user' || userRole === 'USER') {
    return {
      ...baseFields,
    };
  }

  // Public users see minimal fields (only through basic cards)
  return baseFields;
};

/**
 * Filter property object to only include allowed fields for role
 * @param {Object} property - Property object
 * @param {string} userRole - User role
 * @returns {Object} Filtered property object
 */
export const filterPropertyByRole = (property, userRole) => {
  const permissions = getDetailViewPermissions(userRole);
  const filtered = {};

  Object.keys(permissions).forEach((key) => {
    if (permissions[key] && Object.prototype.hasOwnProperty.call(property, key)) {
      filtered[key] = property[key];
    }
  });

  return filtered;
};
