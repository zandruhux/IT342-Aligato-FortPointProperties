/**
 * Role-Based Access Rules
 * Defines permissions and access controls for different user roles
 */

/**
 * User roles constant
 */
export const USER_ROLES = {
  ADMIN: 'ADMIN',
  AGENT: 'AGENT',
  USER: 'USER', // Registered user (not admin/agent)
  PUBLIC: null, // Public user (not logged in)
};

/**
 * Get all permissions for a given role
 * @param {string} userRole - User role
 * @returns {Object} Permissions object
 */
export const getRolePermissions = (userRole) => {
  const basePermissions = {
    canViewProperties: true,
    canViewDetailedView: true,
    canFavoriteProperties: true,
    canSearch: true,
  };

  if (userRole === USER_ROLES.ADMIN) {
    return {
      ...basePermissions,
      canCreateProperty: true,
      canEditProperty: true,
      canDeleteProperty: true,
      canViewAllSearch: true, // Name, Location, Developer
      canViewAgentDetails: true,
      canViewAnalytics: true,
    };
  }

  if (userRole === USER_ROLES.AGENT) {
    return {
      ...basePermissions,
      canCreateProperty: false,
      canEditProperty: false,
      canDeleteProperty: false,
      canViewAllSearch: true, // Name, Location, Developer
      canViewAgentDetails: false,
      canViewAnalytics: false,
    };
  }

  if (userRole === USER_ROLES.USER) {
    return {
      ...basePermissions,
      canCreateProperty: false,
      canEditProperty: false,
      canDeleteProperty: false,
      canViewAllSearch: false, // Limited search
      canViewAgentDetails: false,
      canViewAnalytics: false,
    };
  }

  // Public user (null role)
  return {
    canViewProperties: true,
    canViewDetailedView: false,
    canFavoriteProperties: false,
    canSearch: false, // No search for public
    canCreateProperty: false,
    canEditProperty: false,
    canDeleteProperty: false,
  };
};

/**
 * Get visible search types for a given role
 * @param {string} userRole - User role
 * @returns {Array} Array of available search types
 */
export const getVisibleSearchTypes = (userRole) => {
  if (userRole === USER_ROLES.ADMIN || userRole === USER_ROLES.AGENT) {
    return ['name', 'location', 'developer', 'priceRange'];
  }

  if (userRole === USER_ROLES.USER) {
    return ['name', 'location', 'priceRange'];
  }

  // Public can only search by location
  return ['location'];
};

/**
 * Get visible filter options for a given role
 * @param {string} userRole - User role
 * @returns {Object} Filter options
 */
export const getVisibleFilters = (userRole) => {
  const baseFilters = {
    listingType: true,
    priceRange: true,
    amenities: false,
  };

  if (userRole === USER_ROLES.ADMIN || userRole === USER_ROLES.AGENT) {
    return {
      ...baseFilters,
      developer: true,
    };
  }

  return baseFilters;
};

/**
 * Check if role can access search feature
 * @param {string} userRole - User role
 * @returns {boolean}
 */
export const canAccessSearch = (userRole) => {
  return userRole !== null; // Public cannot search
};

/**
 * Check if role can use advanced filters
 * @param {string} userRole - User role
 * @returns {boolean}
 */
export const canUseAdvancedFilters = (userRole) => {
  return ['ADMIN', 'AGENT'].includes(userRole);
};

/**
 * Get role display name
 * @param {string} userRole - User role
 * @returns {string} Display name
 */
export const getRoleDisplayName = (userRole) => {
  const displayNames = {
    ADMIN: 'Administrator',
    AGENT: 'Real Estate Agent',
    USER: 'User',
  };

  return displayNames[userRole] || 'Guest';
};

/**
 * Check if user role is authenticated
 * @param {string} userRole - User role
 * @returns {boolean}
 */
export const isAuthenticated = (userRole) => {
  return userRole !== null && userRole !== undefined;
};

/**
 * Check if user is admin
 * @param {string} userRole - User role
 * @returns {boolean}
 */
export const isAdmin = (userRole) => {
  return userRole === USER_ROLES.ADMIN;
};

/**
 * Check if user is agent
 * @param {string} userRole - User role
 * @returns {boolean}
 */
export const isAgent = (userRole) => {
  return userRole === USER_ROLES.AGENT;
};

/**
 * Check if user is registered user (not admin/agent)
 * @param {string} userRole - User role
 * @returns {boolean}
 */
export const isRegisteredUser = (userRole) => {
  // Accept both normalized 'USER' and some backends that return 'registered_user'
  return userRole === USER_ROLES.USER || userRole === 'registered_user';
};
