import React from 'react';
import PropertyCard from '../../components/common/PropertyCard';
import FeaturedProperties from '../../components/common/FeaturedProperties';

export default function PropertyListPage() {
  return (
    <div>
      <FeaturedProperties limit={null} title="All Properties" showViewAll={false} />
    </div>
  );
}
