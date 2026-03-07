import React from 'react';

const stats = [
  { value: '2,500+', label: 'Properties Listed' },
  { value: '1,800+', label: 'Happy Clients' },
  { value: '150+', label: 'Expert Agents' },
  { value: '12', label: 'Years Experience' },
];

export default function StatsSection() {
  return (
    <section className="bg-white py-10">
      <div className="container mx-auto px-6">
        <div className="flex justify-around items-center">
          {stats.map((stat, index) => (
            <div key={index} className="text-center">
              <p className="text-3xl font-bold text-blue-600">{stat.value}</p>
              <p className="text-gray-600 text-sm mt-1">{stat.label}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
