import React, { useState, useEffect } from 'react';
import { property } from '../../api/property';

export default function FilterPanel({ onSearch, onClearFilters }) {
  const [searchQuery, setSearchQuery] = useState('');
  const [searchType, setSearchType] = useState('');
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isAgent, setIsAgent] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    // Check authentication and role
    const token = localStorage.getItem('accessToken');
    const role = localStorage.getItem('role');

    setIsAuthenticated(!!token);
    setIsAgent(role === 'AGENT' || role === 'ADMIN');

    // Set default search type based on role
    if (role === 'AGENT' || role === 'ADMIN') {
      setSearchType('name');
    } else if (token) {
      setSearchType('name');
    } else {
      setSearchType('location');
    }
  }, []);

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchQuery.trim()) return;

    setLoading(true);
    try {
      let results = [];

      if (isAgent) {
        // Agent search options
        if (searchType === 'name') {
          results = await property.searchAgentByName(searchQuery);
        } else if (searchType === 'developer') {
          results = await property.searchAgentByDeveloper(searchQuery);
        }
      } else if (isAuthenticated) {
        // Registered user search options
        if (searchType === 'name') {
          results = await property.searchUserByName(searchQuery);
        } else if (searchType === 'location') {
          results = await property.searchUserByLocation(searchQuery);
        }
      } else {
        // Public user search options
        if (searchType === 'location') {
          results = await property.searchPublicByLocation(searchQuery);
        }
      }

      onSearch(results);
    } catch (error) {
      console.error('Search error:', error);
      alert('Search failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleClear = () => {
    setSearchQuery('');
    onClearFilters();
  };

  return (
    <form onSubmit={handleSearch} className="bg-white rounded-lg shadow p-4 mb-8">
      <div className="flex flex-col sm:flex-row gap-3 items-end">
        {/* Search Type Selection */}
        <div className="flex-0.5 min-w-0">
          <select
            value={searchType}
            onChange={(e) => setSearchType(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
            style={{ borderColor: '#d1d5db' }}
          >
            {isAgent && (
              <>
                <option value="name">Property Name</option>
                <option value="developer">Developer Name</option>
              </>
            )}
            {isAuthenticated && !isAgent && (
              <>
                <option value="name">Property Name</option>
                <option value="location">Location</option>
              </>
            )}
            {!isAuthenticated && (
              <option value="location">Location</option>
            )}
          </select>
        </div>

        {/* Search Input */}
        <div className="flex-2 min-w-0">
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder={
              searchType === 'name'
                ? 'Enter property name...'
                : searchType === 'developer'
                ? 'Enter developer name...'
                : 'Enter location...'
            }
            className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
            style={{ borderColor: '#d1d5db' }}
          />
        </div>

        {/* Buttons */}
        <button
          type="submit"
          disabled={loading}
          className="text-white px-6 py-2 rounded-lg font-semibold hover:opacity-90 transition disabled:opacity-50 whitespace-nowrap text-sm"
          style={{ backgroundColor: '#007EB7' }}
        >
          {loading ? 'Searching...' : 'Search'}
        </button>
        <button
          type="button"
          onClick={handleClear}
          className="px-6 py-2 border border-gray-300 rounded-lg font-semibold text-gray-700 hover:bg-gray-50 transition whitespace-nowrap text-sm"
        >
          Clear
        </button>
      </div>
    </form>
  );
}
