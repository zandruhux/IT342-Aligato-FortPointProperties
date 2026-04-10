import React, { useState } from 'react';
import { loginUser } from '../../../api/auth';

export default function LoginForm({ onSwitchToRegister, onLoginSuccess }) {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      const response = await loginUser(formData);

      // Store tokens and user info with new nested structure
      localStorage.setItem('accessToken', response.accessToken);
      localStorage.setItem('refreshToken', response.refreshToken);
      localStorage.setItem('user', JSON.stringify({
        id: response.user.id,
        email: response.user.email,
        firstname: response.user.firstname,
        lastname: response.user.lastname,
        role: response.user.role,
      }));

      setSuccess('Login successful! Redirecting...');

      // Navigate to home page after brief delay
      setTimeout(() => {
        if (onLoginSuccess) onLoginSuccess();
      }, 1500);
    } catch (err) {
      setError(err.error?.message || err.message || 'Login failed. Please check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="w-full max-w-md rounded-lg p-8" style={{ boxShadow: '0 2px 16px rgba(0,0,0,0.10)', backgroundColor: '#FFFFFF' }}>
      <h2 className="text-2xl font-bold mb-2" style={{ color: '#000000' }}>Welcome Back!</h2>
      <p className="text-sm mb-6" style={{ color: '#747474' }}>
        Sign in to access your account and manage your properties
      </p>

      {error && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {error}
        </div>
      )}

      {success && (
        <div className="bg-green-100 border border-green-400 text-green-700 px-4 py-3 rounded mb-4">
          {success}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label htmlFor="email" className="block font-medium mb-2" style={{ color: '#000000' }}>
            Email
          </label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            required
            className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2"
            style={{ borderColor: '#747474', color: '#000000' }}
            placeholder="Enter your email"
          />
        </div>

        <div>
          <label htmlFor="password" className="block font-medium mb-2" style={{ color: '#000000' }}>
            Password
          </label>
          <input
            type="password"
            id="password"
            name="password"
            value={formData.password}
            onChange={handleChange}
            required
            className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2"
            style={{ borderColor: '#747474', color: '#000000' }}
            placeholder="Enter your password"
          />
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full text-white font-semibold py-2 rounded-lg hover:opacity-90 transition disabled:opacity-50 disabled:cursor-not-allowed"
          style={{ backgroundColor: '#007EB7' }}
        >
          {loading ? 'Signing In...' : 'Sign In'}
        </button>
      </form>

      <p className="text-center text-sm mt-6" style={{ color: '#747474' }}>
        Don't have an account?{' '}
        <button
          onClick={onSwitchToRegister}
          className="font-semibold hover:underline"
          style={{ color: '#007EB7' }}
        >
          Sign Up
        </button>
      </p>
    </div>
  );
}
