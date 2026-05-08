import React, { useState } from 'react';
import useAuth from '../hooks/useAuth';

export default function RegistrationForm({ onSwitchToLogin }) {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
  });

  const [success, setSuccess] = useState('');
  const [passwordStrength, setPasswordStrength] = useState({
    score: 0,
    feedback: [],
  });
  const { register, loading, error, setError } = useAuth();

  const validatePasswordStrength = (password) => {
    const feedback = [];
    let score = 0;

    if (password.length >= 8) {
      score++;
    } else {
      feedback.push('at least 8 characters');
    }

    if (/[A-Z]/.test(password)) {
      score++;
    } else {
      feedback.push('an uppercase letter');
    }

    if (/[a-z]/.test(password)) {
      score++;
    } else {
      feedback.push('a lowercase letter');
    }

    if (/\d/.test(password)) {
      score++;
    } else {
      feedback.push('a number');
    }

    if (/[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>/?]/.test(password)) {
      score++;
    } else {
      feedback.push('a special character');
    }

    return { score, feedback };
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    setError('');

    // Update password strength when password changes
    if (name === 'password') {
      setPasswordStrength(validatePasswordStrength(value));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSuccess('');

    // Validation
    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    if (passwordStrength.score < 4) {
      setError('Password is not strong enough');
      return;
    }

    try {
      await register({
        firstname: formData.firstName,
        lastname: formData.lastName,
        email: formData.email,
        password: formData.password,
        confirmPassword: formData.confirmPassword,
      });

      setSuccess('Registration successful! Redirecting to login...');
      setTimeout(() => {
        onSwitchToLogin();
      }, 1500);
    } catch (err) {
      // Error is already set by useAuth hook
      console.error('Registration error:', err);
    }
  };

  const getStrengthColor = () => {
    if (passwordStrength.score === 0) return 'bg-gray-300';
    if (passwordStrength.score <= 2) return 'bg-red-500';
    if (passwordStrength.score === 3) return 'bg-yellow-500';
    return 'bg-green-500';
  };

  const getStrengthText = () => {
    if (passwordStrength.score === 0) return 'None';
    if (passwordStrength.score <= 2) return 'Weak';
    if (passwordStrength.score === 3) return 'Fair';
    if (passwordStrength.score === 4) return 'Good';
    return 'Strong';
  };

  return (
    <div className="w-full max-w-md rounded-lg p-8 pb-8" style={{ boxShadow: '0 2px 16px rgba(0,0,0,0.10)', backgroundColor: '#FFFFFF' }}>
      <h2 className="text-2xl font-bold mb-2" style={{ color: '#000000' }}>Welcome!</h2>
      <p className="text-sm mb-6" style={{ color: '#747474' }}>
        Sign up to access your account and manage your properties
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
          <label htmlFor="firstName" className="block font-medium mb-2" style={{ color: '#000000' }}>
            First Name
          </label>
          <input
            type="text"
            id="firstName"
            name="firstName"
            value={formData.firstName}
            onChange={handleChange}
            required
            className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2"
            style={{ borderColor: '#747474', color: '#000000' }}
            placeholder="Enter your first name"
          />
        </div>

        <div>
          <label htmlFor="lastName" className="block font-medium mb-2" style={{ color: '#000000' }}>
            Last Name
          </label>
          <input
            type="text"
            id="lastName"
            name="lastName"
            value={formData.lastName}
            onChange={handleChange}
            required
            className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2"
            style={{ borderColor: '#747474', color: '#000000' }}
            placeholder="Enter your last name"
          />
        </div>

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
          {formData.password && (
            <div className="mt-2">
              <div className="flex items-center gap-2 mb-1">
                <div className="flex-1 h-2 bg-gray-200 rounded-full overflow-hidden">
                  <div
                    className={`h-full transition-all duration-300 ${getStrengthColor()}`}
                    style={{ width: `${(passwordStrength.score / 5) * 100}%` }}
                  ></div>
                </div>
                <span className="text-sm font-medium">{getStrengthText()}</span>
              </div>
              {passwordStrength.feedback.length > 0 && (
                <p className="text-xs" style={{ color: '#747474' }}>
                  Required: {passwordStrength.feedback.join(', ')}
                </p>
              )}
            </div>
          )}
        </div>

        <div>
          <label htmlFor="confirmPassword" className="block font-medium mb-2" style={{ color: '#000000' }}>
            Confirm Password
          </label>
          <input
            type="password"
            id="confirmPassword"
            name="confirmPassword"
            value={formData.confirmPassword}
            onChange={handleChange}
            required
            className="w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2"
            style={{ borderColor: '#747474', color: '#000000' }}
            placeholder="Confirm your password"
          />
          {formData.confirmPassword && formData.password !== formData.confirmPassword && (
            <p className="text-xs text-red-600 mt-1">Passwords do not match</p>
          )}
        </div>

        <button
          type="submit"
          disabled={loading}
          className="w-full text-white font-semibold py-2 rounded-lg hover:opacity-90 transition disabled:opacity-50 disabled:cursor-not-allowed"
          style={{ backgroundColor: '#007EB7' }}
        >
          {loading ? 'Registering...' : 'Register'}
        </button>
      </form>

      <p className="text-center text-sm mt-6" style={{ color: '#747474' }}>
        Already have an account?{' '}
        <button
          onClick={onSwitchToLogin}
          className="font-semibold hover:underline"
          style={{ color: '#007EB7' }}
        >
          Sign In
        </button>
      </p>
    </div>
  );
}
