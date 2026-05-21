import React from 'react';
import { useNavigate } from 'react-router-dom';
import propertyHeroBg from '../../../assets/property-hero-bg.png';
import PropertySearchFilter from '../properties/PropertySearchFilter';

const clientAvatars = [
  {
    alt: 'Smiling homeowner client',
    src: 'https://randomuser.me/api/portraits/women/44.jpg'
  },
  {
    alt: 'Satisfied property buyer',
    src: 'https://randomuser.me/api/portraits/men/32.jpg'
  },
  {
    alt: 'Real estate investor client',
    src: 'https://randomuser.me/api/portraits/women/68.jpg'
  },
  {
    alt: 'Happy Fort Point client',
    src: 'https://randomuser.me/api/portraits/men/75.jpg'
  }
];

export default function HeroSection({ showSearch = false, variant = 'default' }) {
  const navigate = useNavigate();
  const isAuthVariant = variant === 'auth';

  const handleHeroSearch = (searchTerm, searchType) => {
    const params = new URLSearchParams();
    if (searchTerm) {
      params.set('searchType', searchType);
      params.set('searchTerm', searchTerm);
    }
    navigate(`/properties${params.toString() ? `?${params.toString()}` : ''}`);
  };

  if (isAuthVariant) {
    return <HeroCopy />;
  }

  return (
    <section className="relative min-h-[calc(100vh-88px)] overflow-hidden bg-blue-950">
      <img
        src={propertyHeroBg}
        alt="Modern residential property"
        className="absolute inset-0 h-full w-full object-cover"
      />
      <div
        className="absolute inset-0"
        style={{
          background: 'linear-gradient(90deg, rgba(0, 126, 183, 0.68) 0%, rgba(0, 126, 183, 0.46) 34%, rgba(0, 126, 183, 0.18) 62%, rgba(0, 126, 183, 0.03) 100%)',
        }}
      />
      <div
        className="absolute inset-0"
        style={{
          background: 'linear-gradient(0deg, rgba(0, 73, 117, 0.2) 0%, rgba(255, 255, 255, 0.08) 62%, rgba(255, 255, 255, 0.2) 100%)',
        }}
      />
      <div className="relative z-10 mx-auto flex min-h-[calc(100vh-88px)] max-w-7xl items-center px-4 py-16 sm:px-6 lg:px-8">
        <div className="max-w-xl lg:max-w-2xl">
          <HeroCopy />

          {showSearch && (
            <div className="mt-10">
              <PropertySearchFilter
                onSearch={handleHeroSearch}
                showSort={false}
                title=""
              />
            </div>
          )}
        </div>
      </div>
    </section>
  );
}

export function HeroBackdrop({ children }) {
  return (
    <section className="relative min-h-[calc(100vh-88px)] overflow-hidden bg-blue-950">
      <img
        src={propertyHeroBg}
        alt="Modern residential property"
        className="absolute inset-0 h-full w-full object-cover"
      />
      <div
        className="absolute inset-0"
        style={{
          background: 'linear-gradient(90deg, rgba(255, 255, 255, 0.96) 0%, rgba(235, 246, 255, 0.88) 26%, rgba(0, 126, 183, 0.3) 56%, rgba(0, 126, 183, 0.12) 100%)',
        }}
      />
      <div
        className="absolute inset-0"
        style={{
          background: 'linear-gradient(0deg, rgba(0, 126, 183, 0.14) 0%, rgba(255, 255, 255, 0.04) 70%)',
        }}
      />
      <div
        className="absolute inset-0"
        style={{
          background: 'linear-gradient(90deg, rgba(0, 126, 183, 0.18) 0%, rgba(0, 126, 183, 0.16) 100%)',
        }}
      />
      <div className="relative z-10 mx-auto grid min-h-[calc(100vh-88px)] max-w-7xl grid-cols-1 items-center gap-10 px-4 py-12 sm:px-6 lg:grid-cols-[minmax(320px,420px)_minmax(360px,560px)] lg:items-start lg:px-8 lg:py-16">
        {children}
      </div>
    </section>
  );
}

function HeroCopy({ tone = 'light' }) {
  const isDark = tone === 'dark';

  return (
    <div className={isDark ? 'text-slate-950' : 'text-white drop-shadow-sm'}>
      <h1 className="mb-5 text-4xl font-bold leading-tight lg:text-5xl">
        Find Your
        <br />
        <span className="font-black text-[#1A8FC2]">Perfect</span> Home
      </h1>
      <p className={`mb-8 text-base leading-relaxed lg:text-lg ${isDark ? 'text-slate-700' : 'text-white/90'}`}>
        Discover exceptional properties across the Philippines. Whether you're buying,
        selling, or investing, our expert team is here to guide you every step of the way.
      </p>
      <div className="flex flex-col gap-4">
        <div className="flex items-center">
          {clientAvatars.map((avatar, index) => (
            <img
              key={avatar.alt}
              src={avatar.src}
              alt={avatar.alt}
              referrerPolicy="no-referrer"
              className={`h-12 w-12 rounded-full border-2 border-white object-cover bg-slate-200 shadow-lg ${
                index > 0 ? '-ml-3' : ''
              }`}
            />
          ))}
        </div>
        <div>
          <p className="text-lg font-bold">Trusted by 1,800+ clients</p>
          <p className={`text-sm ${isDark ? 'text-slate-600' : 'text-white/80'}`}>Join our satisfied homeowners</p>
        </div>
      </div>
    </div>
  );
}
