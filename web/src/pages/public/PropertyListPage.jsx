import React, { useState } from 'react';
import PropertyCard from '../../components/common/PropertyCard';
import FeaturedProperties from '../../components/common/FeaturedProperties';
import FilterPanel from '../../components/common/FilterPanel';

export default function PropertyListPage() {
  const [searchResults, setSearchResults] = useState(null);
  const [hasSearched, setHasSearched] = useState(false);

  const handleSearch = (results) => {
    setSearchResults(results);
    setHasSearched(true);
  };

  const handleClearFilters = () => {
    setSearchResults(null);
    setHasSearched(false);
  };

  return (
    <div className="bg-gray-50 py-12">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* Header Section */}
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">All Properties</h1>
          <p className="text-gray-600 text-lg">Handpicked properties curated for discerning buyers</p>
        </div>

        {/* Search Filter */}
        <FilterPanel onSearch={handleSearch} onClearFilters={handleClearFilters} />

        {/* Properties Grid */}
        {hasSearched ? (
          <div>
            <h2 className="text-2xl font-bold mb-6 text-gray-900">Search Results</h2>
            {searchResults && searchResults.length > 0 ? (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {searchResults.map((property) => (
                  <PropertyCard key={property.id} property={property} />
                ))}
              </div>
            ) : (
              <div className="text-center py-12">
                <p className="text-gray-500 text-lg">No properties found matching your search.</p>
              </div>
            )}
          </div>
        ) : (
          <FeaturedProperties limit={null} title="" showViewAll={false} />
        )}
      </div>
    </div>
  );
}
