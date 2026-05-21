import React, { useEffect, useState } from 'react';
import { FcGoogle } from 'react-icons/fc';
import useAuth from '../hooks/useAuth';
import authApi from '../api/authApi';

export default function LoginForm({ onSwitchToRegister, onLoginSuccess, initialError = '' }) {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });

  const [success, setSuccess] = useState('');
  const [oauthError, setOauthError] = useState(initialError);
  const { login, loading, error, setError } = useAuth();

  useEffect(() => {
    setOauthError(initialError);
  }, [initialError]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    setError('');
    setOauthError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSuccess('');
    setOauthError('');

    try {
      const userData = await login(formData);
      setSuccess('Login successful! Redirecting...');

      // Navigate to home page after brief delay
      setTimeout(() => {
        if (onLoginSuccess) onLoginSuccess(userData);
      }, 1500);
    } catch (err) {
      // Error is already set by useAuth hook
      console.error('Login error:', err);
    }
  };

  const handleGoogleSignIn = () => {
    window.location.href = authApi.getGoogleAuthUrl();
  };

  const displayError = error || oauthError;

  return (
    <div className="w-full max-w-md min-h-[470px] rounded-lg p-10 flex flex-col justify-center" style={{ boxShadow: '0 2px 16px rgba(0,0,0,0.10)', backgroundColor: '#FFFFFF' }}>
      <h2 className="text-2xl font-bold mb-2" style={{ color: '#000000' }}>Welcome Back!</h2>
      <p className="text-sm mb-6" style={{ color: '#747474' }}>
        Sign in to access your account and manage your properties
      </p>

      {displayError && (
        <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
          {displayError}
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

      <div className="flex items-center gap-3 my-5">
        <div className="h-px flex-1 bg-gray-200" />
        <span className="text-xs font-medium uppercase" style={{ color: '#747474' }}>or</span>
        <div className="h-px flex-1 bg-gray-200" />
      </div>

      <button
        type="button"
        onClick={handleGoogleSignIn}
        className="w-full flex items-center justify-center gap-3 border font-semibold py-2 rounded-lg hover:bg-gray-50 transition"
        style={{ borderColor: '#DADCE0', color: '#1F1F1F', backgroundColor: '#FFFFFF' }}
      >
        <FcGoogle className="h-5 w-5" aria-hidden="true" />
        Sign in with Google
      </button>

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
