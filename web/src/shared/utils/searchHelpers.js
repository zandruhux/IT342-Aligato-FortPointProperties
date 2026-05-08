/**
 * Search Helper Functions
 * Pure utility functions for search operations and data transformations
 */

/**
 * Normalize search term - trim and lowercase for case-insensitive matching
 * @param {string} term - Raw search term
 * @returns {string} Normalized search term
 */
export const normalizeSearchTerm = (term) => {
  if (!term) return '';
  return term.trim().toLowerCase();
};

/**
 * Check if a string matches a search term (case-insensitive, partial match)
 * @param {string} text - Text to search in
 * @param {string} searchTerm - Search term
 * @returns {boolean} True if matches
 */
export const matchesSearchTerm = (text, searchTerm) => {
  if (!text || !searchTerm) return true; // No search term means match all
  const normalized = normalizeSearchTerm(text);
  const search = normalizeSearchTerm(searchTerm);
  return normalized.includes(search);
};

/**
 * Filter properties by name
 * @param {Array} properties - Array of property objects
 * @param {string} searchTerm - Name search term
 * @returns {Array} Filtered properties
 */
export const filterByName = (properties, searchTerm) => {
  if (!searchTerm) return properties;
  return properties.filter((prop) => matchesSearchTerm(prop.name, searchTerm));
};

/**
 * Filter properties by location
 * @param {Array} properties - Array of property objects
 * @param {string} searchTerm - Location search term
 * @returns {Array} Filtered properties
 */
export const filterByLocation = (properties, searchTerm) => {
  if (!searchTerm) return properties;
  return properties.filter((prop) => matchesSearchTerm(prop.location, searchTerm));
};

/**
 * Filter properties by developer
 * @param {Array} properties - Array of property objects
 * @param {string} searchTerm - Developer search term
 * @returns {Array} Filtered properties
 */
export const filterByDeveloper = (properties, searchTerm) => {
  if (!searchTerm) return properties;
  return properties.filter((prop) => matchesSearchTerm(prop.developer, searchTerm));
};

/**
 * Filter properties by price range
 * @param {Array} properties - Array of property objects
 * @param {number} minPrice - Minimum price (inclusive)
 * @param {number} maxPrice - Maximum price (inclusive)
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
 * Apply multiple filters to properties (AND logic)
 * @param {Array} properties - Array of property objects
 * @param {Object} filters - Filter object { name, location, developer, minPrice, maxPrice }
 * @returns {Array} Filtered properties
 */
export const applySearchFilters = (properties, filters = {}) => {
  let results = properties;

  if (filters.name) {
    results = filterByName(results, filters.name);
  }

  if (filters.location) {
    results = filterByLocation(results, filters.location);
  }

  if (filters.developer) {
    results = filterByDeveloper(results, filters.developer);
  }

  if (filters.minPrice !== null || filters.maxPrice !== null) {
    results = filterByPriceRange(results, filters.minPrice, filters.maxPrice);
  }

  return results;
};

/**
 * Check if any search filters are active
 * @param {Object} filters - Filter object
 * @returns {boolean} True if any filter is applied
 */
export const hasActiveFilters = (filters = {}) => {
  return !!(
    filters.name ||
    filters.location ||
    filters.developer ||
    filters.minPrice !== null ||
    filters.maxPrice !== null
  );
};

/**
 * Get default filter state
 * @returns {Object} Default filter object
 */
export const getDefaultFilters = () => {
  return {
    name: '',
    location: '',
    developer: '',
    minPrice: null,
    maxPrice: null,
  };
};

/**
 * Merge and deduplicate search results from multiple sources
 * @param {Array} results1 - First array of results
 * @param {Array} results2 - Second array of results
 * @param {string} idField - Field to use for deduplication (default: 'id')
 * @returns {Array} Merged and deduplicated results
 */
export const mergeSearchResults = (results1 = [], results2 = [], idField = 'id') => {
  const merged = [...results1, ...results2];
  const seen = new Set();
  return merged.filter((item) => {
    const id = item[idField];
    if (seen.has(id)) return false;
    seen.add(id);
    return true;
  });
};

/**
 * Sort search results by relevance (how well they match the search term)
 * @param {Array} properties - Array of property objects
 * @param {string} searchTerm - Search term used
 * @param {string} fieldName - Field name to sort by
 * @returns {Array} Sorted properties
 */
export const sortByRelevance = (properties, searchTerm, fieldName = 'name') => {
  if (!searchTerm) return properties;

  const normalized = normalizeSearchTerm(searchTerm);

  return [...properties].sort((a, b) => {
    const aText = normalizeSearchTerm(a[fieldName]);
    const bText = normalizeSearchTerm(b[fieldName]);

    // Exact match comes first
    if (aText === normalized && bText !== normalized) return -1;
    if (aText !== normalized && bText === normalized) return 1;

    // Starts with search term comes second
    if (aText.startsWith(normalized) && !bText.startsWith(normalized)) return -1;
    if (!aText.startsWith(normalized) && bText.startsWith(normalized)) return 1;

    // Otherwise maintain original order
    return 0;
  });
};
