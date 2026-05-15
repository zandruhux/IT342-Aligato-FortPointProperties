import React, { useState } from 'react';
import { FiSend } from 'react-icons/fi';

export default function MessageInput({ disabled, placeholder = 'Write a message', onSend }) {
  const [content, setContent] = useState('');

  const submit = async (event) => {
    event.preventDefault();
    const trimmed = content.trim();
    if (!trimmed || disabled) {
      return;
    }
    await onSend(trimmed);
    setContent('');
  };

  return (
    <form onSubmit={submit} className="flex gap-3 border-t border-gray-200 bg-white p-4">
      <textarea
        value={content}
        onChange={(event) => setContent(event.target.value)}
        disabled={disabled}
        rows={2}
        placeholder={placeholder}
        className="min-h-12 flex-1 resize-none rounded border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-100 disabled:bg-gray-100"
      />
      <button
        type="submit"
        disabled={disabled || !content.trim()}
        title="Send message"
        className="flex h-12 w-12 items-center justify-center rounded bg-blue-600 text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:bg-gray-300"
      >
        <FiSend aria-hidden="true" />
      </button>
    </form>
  );
}
