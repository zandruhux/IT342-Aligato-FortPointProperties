import React from 'react';
import HeroSection from '../components/common/HeroSection';
import StatsSection from '../components/dashboard/StatsSection';

export default function HomePage() {
  return (
    <div>
      <HeroSection showSearch={true} />
      <StatsSection />
    </div>
  );
}
