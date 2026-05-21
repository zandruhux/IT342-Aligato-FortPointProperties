import React, { useState } from 'react';
import Modal from '../../../shared/components/ui/Modal';
import Button from '../../../shared/components/ui/Button';

const initialForm = {
  firstname: '',
  lastname: '',
  email: '',
  password: '',
  role: 'REGISTERED_USER',
};

export default function AddUserModal({ isOpen, onClose, onCreate, loading }) {
  const [form, setForm] = useState(initialForm);
  const [error, setError] = useState('');

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    try {
      await onCreate(form);
      setForm(initialForm);
      onClose();
    } catch (err) {
      setError(err?.message || 'Unable to create user.');
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Add User" size="lg">
      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          <input className="border border-gray-300 rounded-lg px-3 py-2" placeholder="First name" value={form.firstname} onChange={(e) => setForm({ ...form, firstname: e.target.value })} required />
          <input className="border border-gray-300 rounded-lg px-3 py-2" placeholder="Last name" value={form.lastname} onChange={(e) => setForm({ ...form, lastname: e.target.value })} required />
        </div>
        <input className="w-full border border-gray-300 rounded-lg px-3 py-2" type="email" placeholder="Email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required />
        <input className="w-full border border-gray-300 rounded-lg px-3 py-2" type="password" placeholder="Password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required />
        <select className="w-full border border-gray-300 rounded-lg px-3 py-2" value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}>
          <option value="REGISTERED_USER">Registered User</option>
          <option value="AGENT">Agent</option>
          <option value="ADMIN">Admin</option>
        </select>
        {error && <p className="text-sm text-red-600 font-semibold">{error}</p>}
        <div className="flex justify-end gap-2">
          <Button type="button" variant="secondary" onClick={onClose}>Cancel</Button>
          <Button type="submit" loading={loading}>Create User</Button>
        </div>
      </form>
    </Modal>
  );
}
