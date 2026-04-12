import React, { useState } from 'react';

const PropertySearchFilter = ({ onSearch, onFilter, onClearFilters, isLoading }) => {
  const [searchType, setSearchType] = useState('name'); // 'name', 'listing-type', 'developer'
  const [searchValue, setSearchValue] = useState('');
  const [listingTypeOptions] = useState([
    'Sale',
    'Rent',
    'Pre-selling'
  ]);
  const [developerOptions] = useState([
    'Ayala Land',
    'Megaworld',
    'Damosa Land',
    'Other Developers'
  ]);

  const handleSearch = () => {
    if (!searchValue.trim()) {
      alert('Please enter a search value');
      return;
    }

    if (searchType === 'name') {
      onSearch('name', searchValue);
    } else if (searchType === 'listing-type') {
      onFilter('listing-type', searchValue);
    } else if (searchType === 'developer') {
      onSearch('developer', searchValue);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  const handleClear = () => {
    setSearchValue('');
    setSearchType('name');
    onClearFilters();
  };

  return (
    <div className="bg-gradient-to-r from-blue-600 to-blue-700 rounded-lg p-8 shadow-lg mb-10">
      <h2 className="text-2xl font-bold text-white mb-2">Search Properties</h2>
      <p className="text-blue-100 text-sm mb-6 font-medium">Find the perfect property with advanced filters</p>
      
      <div className="flex flex-col md:flex-row gap-4">
        {/* Search Type Selector */}
        <select
          value={searchType}
          onChange={(e) => {
            setSearchType(e.target.value);
            setSearchValue('');
          }}
          className="px-4 py-3 rounded-lg border-none font-medium text-gray-700 focus:outline-none focus:ring-2 focus:ring-blue-400 flex-shrink-0 bg-white shadow-md"
        >
          <option value="name">Search by Name</option>
          <option value="listing-type">Filter by Listing Type</option>
          <option value="developer">Search by Developer</option>
        </select>

        {/* Dynamic Input/Select based on search type */}
        {searchType === 'name' && (
          <input
            type="text"
            placeholder="Enter property name..."
            value={searchValue}
            onChange={(e) => setSearchValue(e.target.value)}
            onKeyPress={handleKeyPress}
            className="flex-1 px-4 py-3 rounded-lg border-none focus:outline-none focus:ring-2 focus:ring-blue-400 bg-white shadow-md font-medium"
          />
        )}

        {searchType === 'listing-type' && (
          <select
            value={searchValue}
            onChange={(e) => setSearchValue(e.target.value)}
            className="flex-1 px-4 py-3 rounded-lg border-none focus:outline-none focus:ring-2 focus:ring-blue-400 text-gray-700 font-medium bg-white shadow-md"
          >
            <option value="">Select Listing Type</option>
            {listingTypeOptions.map((type) => (
              <option key={type} value={type}>
                {type}
              </option>
            ))}
          </select>
        )}

        {searchType === 'developer' && (
          <select
            value={searchValue}
            onChange={(e) => setSearchValue(e.target.value)}
            className="flex-1 px-4 py-3 rounded-lg border-none focus:outline-none focus:ring-2 focus:ring-blue-400 text-gray-700 font-medium bg-white shadow-md"
          >
            <option value="">Select Developer</option>
            {developerOptions.map((dev) => (
              <option key={dev} value={dev}>
                {dev}
              </option>
            ))}
          </select>
        )}

        {/* Search Button */}
        <button
          onClick={handleSearch}
          disabled={isLoading}
          className="bg-white hover:bg-blue-50 disabled:bg-gray-300 text-blue-600 font-bold py-3 px-8 rounded-lg transition-all duration-200 flex-shrink-0 shadow-md hover:shadow-lg font-semibold"
        >
          {isLoading ? 'Searching...' : 'Search'}
        </button>

        {/* Clear Filters Button */}
        <button
          onClick={handleClear}
          className="bg-blue-500 hover:bg-blue-400 text-white font-bold py-3 px-8 rounded-lg transition-all duration-200 flex-shrink-0 shadow-md hover:shadow-lg font-semibold"
        >
          Clear
        </button>
      </div>
    </div>
  );
};

export default PropertySearchFilter;
