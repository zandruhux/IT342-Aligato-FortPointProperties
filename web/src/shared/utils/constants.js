export const API_BASE_URL = 'http://localhost:8080';
export const WS_BASE_URL = `${API_BASE_URL}/ws`;

export const ROLES = {
  ADMIN: 'ADMIN',
  AGENT: 'AGENT',
  USER: 'USER',
  REGISTERED_USER: 'REGISTERED_USER',
  PUBLIC: 'PUBLIC',
};

export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  CONFLICT: 409,
  INTERNAL_SERVER_ERROR: 500,
};

export const API_ENDPOINTS = {
  // Auth
  AUTH: {
    REGISTER: '/api/v1/auth/register',
    LOGIN: '/api/v1/auth/login',
    PROFILE: '/api/v1/auth/profile',
    USERS: '/api/v1/auth/users',
  },

  // Public Properties (No Auth)
  PROPERTIES_PUBLIC: {
    ALL: '/properties',
    SEARCH: '/properties/search',
    BY_ID: (id) => `/properties/${id}`,
  },

  // Registered User Properties (Auth Required)
  PROPERTIES_USER: {
    ALL: '/user/properties',
    DETAILS: (id) => `/user/properties/${id}/advanced`,
    SEARCH_NAME: '/user/properties/search/name',
    SEARCH_LOCATION: '/user/properties/search/location',
    SEARCH: '/user/properties/search',
  },

  // Agent Properties (Auth Required + Agent Role)
  PROPERTIES_AGENT: {
    ALL: '/agent/properties',
    DETAILS: (id) => `/agent/properties/${id}/advanced`,
    SEARCH_NAME: '/agent/properties/search/name',
    SEARCH_LOCATION: '/agent/properties/search/location',
    SEARCH_DEVELOPER: '/agent/properties/search/developer',
    SEARCH: '/agent/properties/search',
  },

  // Admin Properties (Auth Required + Admin Role)
  PROPERTIES_ADMIN: {
    ALL: '/admin/properties',
    BY_ID: (id) => `/admin/properties/${id}`,
    CREATE: '/admin/properties',
    UPDATE: (id) => `/admin/properties/${id}`,
    DELETE: (id) => `/admin/properties/${id}`,
    SEARCH_NAME: '/admin/properties/search/name',
    SEARCH_LOCATION: '/admin/properties/search/location',
    SEARCH_DEVELOPER: '/admin/properties/search/developer',
    SEARCH: '/admin/properties/search',
    UNITS: {
      ALL: (propertyId) => `/admin/properties/${propertyId}/units`,
      CREATE: (propertyId) => `/admin/properties/${propertyId}/units`,
      UPDATE: (propertyId, unitId) => `/admin/properties/${propertyId}/units/${unitId}`,
      DELETE: (propertyId, unitId) => `/admin/properties/${propertyId}/units/${unitId}`,
    },
  },

  // Favorites (Auth Required)
  FAVORITES: {
    ALL: '/user/favorites',
    ADD: (propertyId) => `/user/favorites/${propertyId}`,
    REMOVE: (propertyId) => `/user/favorites/${propertyId}`,
    CHECK: (propertyId) => `/user/favorites/${propertyId}/check`,
    COUNT: '/user/favorites/count',
  },

  MESSAGING: {
    CONVERSATIONS: '/api/messaging/conversations',
    MESSAGES: (conversationId) => `/api/messaging/conversations/${conversationId}/messages`,
  },
};
