import React, { useCallback, useEffect, useState } from 'react';
import { FiMessageCircle, FiX } from 'react-icons/fi';
import { useAuthContext } from '../../../shared/context/useAuthContext';
import { createConversation, getConversations } from '../api';
import { ConversationList } from '../components';
import { useMessagingSocket } from '../hooks';
import { mergeConversationEvent, withDisplayName } from '../utils/messagingHelpers';
import { ConversationPage } from './';

export default function RegisteredUserMessagesPage() {
  const { user } = useAuthContext();
  const [conversations, setConversations] = useState([]);
  const [selected, setSelected] = useState(null);
  const [incomingMessage, setIncomingMessage] = useState(null);
  const [content, setContent] = useState('');
  const [error, setError] = useState('');
  const [showNewConversation, setShowNewConversation] = useState(false);
  const unreadTotal = conversations.reduce((total, conversation) => total + (conversation.unreadCount || 0), 0);

  const updateConversationPreview = useCallback((event, unread) => {
    setConversations((current) => current.map((conversation) => (
      conversation.id === event.conversationId
        ? mergeConversationEvent(conversation, event, unread, false)
        : conversation
    )));
    setSelected((current) => (
      current?.id === event.conversationId
        ? mergeConversationEvent(current, event, unread, false)
        : current
    ));
  }, []);

  const handleSocketMessage = useCallback((event) => {
    updateConversationPreview(event, selected?.id !== event.conversationId);
    if (selected?.id === event.conversationId) {
      setIncomingMessage(event);
    }
  }, [selected?.id, updateConversationPreview]);

  const loadConversations = useCallback(async () => {
    try {
      const data = await getConversations();
      const displayData = data.map(withDisplayName);
      setConversations(displayData);
      setSelected((current) => {
        if (!current) {
          return displayData[0] || null;
        }
        return displayData.find((conversation) => conversation.id === current.id) || displayData[0] || null;
      });
      setError('');
    } catch (err) {
      setError(err?.error || err?.message || 'Unable to load conversations');
    }
  }, []);

  useEffect(() => {
    loadConversations();
  }, [loadConversations]);

  useMessagingSocket({
    user,
    role: user?.role,
    onMessage: handleSocketMessage,
  });

  const startConversation = async (event) => {
    event.preventDefault();
    const trimmed = content.trim();
    if (!trimmed) {
      return;
    }

    try {
      const conversation = await createConversation({ content: trimmed });
      const displayConversation = withDisplayName(conversation);
      setConversations((current) => [displayConversation, ...current]);
      setSelected(displayConversation);
      setContent('');
      setShowNewConversation(false);
      setError('');
    } catch (err) {
      setError(err?.error || err?.message || 'Unable to start conversation');
    }
  };

  const submitOnEnter = (event) => {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      event.currentTarget.form?.requestSubmit();
    }
  };

  return (
    <div className="bg-gray-50 py-10">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Messages</h1>
          <p className="mt-2 text-gray-600">
            Start a conversation with an available agent.
            {unreadTotal > 0 && (
              <span className="ml-3 inline-flex items-center rounded-full bg-red-600 px-2 py-0.5 text-xs font-bold text-white">
                {unreadTotal} unread
              </span>
            )}
          </p>
        </div>

        {error && <div className="mb-6 rounded border border-red-200 bg-red-50 p-4 text-sm text-red-700">{error}</div>}

        <div className="mb-6">
          {!showNewConversation ? (
            <button
              type="button"
              onClick={() => setShowNewConversation(true)}
              className="inline-flex items-center gap-2 rounded bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700"
            >
              <FiMessageCircle aria-hidden="true" />
              Chat with a new agent
            </button>
          ) : (
            <form onSubmit={startConversation} className="rounded border border-gray-200 bg-white p-4">
              <div className="mb-3 flex items-center justify-between gap-3">
                <label className="block text-sm font-semibold text-gray-800" htmlFor="new-message">
                  Chat with a new agent
                </label>
                <button
                  type="button"
                  title="Cancel"
                  onClick={() => {
                    setShowNewConversation(false);
                    setContent('');
                  }}
                  className="flex h-8 w-8 items-center justify-center rounded text-gray-500 hover:bg-gray-100 hover:text-gray-700"
                >
                  <FiX aria-hidden="true" />
                </button>
              </div>
              <div className="flex gap-3">
                <textarea
                  id="new-message"
                  value={content}
                  onChange={(event) => setContent(event.target.value)}
                  onKeyDown={submitOnEnter}
                  rows={2}
                  className="flex-1 resize-none rounded border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-100"
                  placeholder="Ask about a property or schedule"
                />
                <button
                  type="submit"
                  disabled={!content.trim()}
                  className="rounded bg-blue-600 px-5 py-2 text-sm font-semibold text-white hover:bg-blue-700 disabled:bg-gray-300"
                >
                  Start
                </button>
              </div>
            </form>
          )}
        </div>

        <div className="grid gap-6 lg:grid-cols-[22rem_1fr]">
          <ConversationList
            conversations={conversations}
            activeId={selected?.id}
            onSelect={(conversation) => {
              setSelected({ ...conversation, unread: false });
              setConversations((current) => current.map((item) => (
                item.id === conversation.id ? { ...item, unread: false, unreadCount: 0 } : item
              )));
            }}
            emptyText="No conversations yet"
            showStatus={false}
            currentUser={user}
          />
          <ConversationPage
            conversation={selected}
            user={user}
            role={user?.role}
            onConversationLocked={loadConversations}
            onMessageSent={(message) => updateConversationPreview(message, false)}
            incomingMessage={incomingMessage}
            showStatus={false}
          />
        </div>
      </div>
    </div>
  );
}
