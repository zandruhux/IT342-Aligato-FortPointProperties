import { useState } from 'react';
import { FiSearch, FiFilter, FiX } from 'react-icons/fi';

export default function PropertySearchFilter({ 
  onSearch, 
  onFilter, 
  onClearFilters,
  isLoading 
}) {
  const [searchTerm, setSearchTerm] = useState('');
  const [filterType, setFilterType] = useState('');
  const [searchBy, setSearchBy] = useState('name');

  const handleSearch = () => {
    if (searchTerm.trim()) {
      onSearch(searchTerm, searchBy);
    }
  };

  const handleFilter = (type) => {
    setFilterType(type);
    onFilter(type);
  };

  const handleClear = () => {
    setSearchTerm('');
    setFilterType('');
    setSearchBy('name');
    onClearFilters();
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  return (
    <div className="bg-gradient-to-r from-blue-600 to-blue-700 rounded-lg p-6 shadow-lg mb-8">
      {/* Title */}
      <h3 className="text-white text-lg font-bold mb-4 flex items-center gap-2 drop-shadow">
        <FiFilter size={20} />
        Search & Filter Properties
      </h3>

      {/* Search Section */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-3 mb-4">
        <div className="md:col-span-2">
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="Search properties..."
            className="w-full px-4 py-3 rounded-lg font-medium text-slate-900 placeholder-slate-500 focus:outline-none focus:ring-2 focus:ring-white focus:ring-opacity-50 shadow-sm"
          />
        </div>
        <select
          value={searchBy}
          onChange={(e) => setSearchBy(e.target.value)}
          className="px-4 py-3 rounded-lg font-bold text-slate-900 bg-white focus:outline-none focus:ring-2 focus:ring-white focus:ring-opacity-50 shadow-sm"
        >
          <option value="name">Search by Name</option>
          <option value="location">Search by Location</option>
          <option value="developer">Search by Developer</option>
        </select>
        <button
          onClick={handleSearch}
          disabled={isLoading || !searchTerm.trim()}
          className="bg-white text-blue-600 font-bold px-4 py-3 rounded-lg hover:bg-blue-50 transition-colors duration-200 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2 shadow-sm"
        >
          <FiSearch size={18} />
          Search
        </button>
      </div>

      {/* Filter Section */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-3">
        <button
          onClick={() => handleFilter('Pre-Selling')}
          className={`px-4 py-2 rounded-lg font-bold transition-all duration-200 ${
            filterType === 'Pre-Selling'
              ? 'bg-white text-blue-700 shadow-lg'
              : 'bg-blue-400 text-white hover:bg-blue-500'
          }`}
        >
          Pre-Selling
        </button>
        <button
          onClick={() => handleFilter('RFO')}
          className={`px-4 py-2 rounded-lg font-bold transition-all duration-200 ${
            filterType === 'RFO'
              ? 'bg-white text-blue-700 shadow-lg'
              : 'bg-blue-400 text-white hover:bg-blue-500'
          }`}
        >
          RFO (Ready-For-Occupancy)
        </button>
        <button
          onClick={handleClear}
          className="px-4 py-2 bg-white text-blue-700 rounded-lg font-semibold hover:bg-blue-50 transition-all duration-200 flex items-center justify-center gap-2 shadow-sm"
        >
          <FiX size={18} />
          Clear
        </button>
        <div className="text-white text-sm font-bold flex items-center justify-end">
          {filterType && <span className="bg-white text-blue-700 px-3 py-1 rounded-full font-bold shadow-sm">Filtering: {filterType}</span>}
        </div>
      </div>
    </div>
  );
}
