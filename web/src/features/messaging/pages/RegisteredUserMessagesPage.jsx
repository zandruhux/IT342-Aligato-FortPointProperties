import React, { useCallback, useEffect, useState } from 'react';
import { useAuthContext } from '../../../shared/context/useAuthContext';
import { createConversation, getConversations } from '../api/messagingApi';
import { ConversationList } from '../components';
import { useMessagingSocket } from '../hooks/useMessagingSocket';
import ConversationPage from './ConversationPage';

const withDisplayName = (conversation) => ({
  ...conversation,
  displayName: conversation.assignedAgentName
    || (conversation.latestMessageSenderId === conversation.assignedAgentId ? conversation.latestMessageSenderName : '')
    || 'Fort Point Properties',
});

export default function RegisteredUserMessagesPage() {
  const { user } = useAuthContext();
  const [conversations, setConversations] = useState([]);
  const [selected, setSelected] = useState(null);
  const [content, setContent] = useState('');
  const [error, setError] = useState('');

  const updateConversationPreview = useCallback((event, unread) => {
    setConversations((current) => current.map((conversation) => (
      conversation.id === event.conversationId
        ? {
            ...conversation,
            assignedAgentName: event.senderRole === 'AGENT'
              ? event.senderName || conversation.assignedAgentName
              : conversation.assignedAgentName,
            registeredUserName: event.senderRole === 'REGISTERED_USER'
              ? event.senderName || conversation.registeredUserName
              : conversation.registeredUserName,
            displayName: event.senderRole === 'AGENT'
              ? event.senderName || conversation.displayName
              : conversation.displayName,
            latestMessagePreview: event.content,
            latestMessageSenderId: event.senderId,
            latestMessageSenderName: event.senderName,
            latestMessageAt: event.createdAt,
            unread,
          }
        : conversation
    )));
    setSelected((current) => (
      current?.id === event.conversationId
        ? {
            ...current,
            assignedAgentName: event.senderRole === 'AGENT'
              ? event.senderName || current.assignedAgentName
              : current.assignedAgentName,
            registeredUserName: event.senderRole === 'REGISTERED_USER'
              ? event.senderName || current.registeredUserName
              : current.registeredUserName,
            displayName: event.senderRole === 'AGENT'
              ? event.senderName || current.displayName
              : current.displayName,
          }
        : current
    ));
  }, []);

  const handleSocketMessage = useCallback((event) => {
    updateConversationPreview(event, selected?.id !== event.conversationId);
  }, [selected?.id, updateConversationPreview]);

  const loadConversations = useCallback(async () => {
    try {
      const data = await getConversations();
      setConversations(data.map(withDisplayName));
      if (!selected && data.length) {
        setSelected(withDisplayName(data[0]));
      }
      setError('');
    } catch (err) {
      setError(err?.error || err?.message || 'Unable to load conversations');
    }
  }, [selected]);

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
      setError('');
    } catch (err) {
      setError(err?.error || err?.message || 'Unable to start conversation');
    }
  };

  return (
    <div className="bg-gray-50 py-10">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">Messages</h1>
          <p className="mt-2 text-gray-600">Start a conversation with an available agent.</p>
        </div>

        {error && <div className="mb-6 rounded border border-red-200 bg-red-50 p-4 text-sm text-red-700">{error}</div>}

        <form onSubmit={startConversation} className="mb-6 rounded border border-gray-200 bg-white p-4">
          <label className="mb-2 block text-sm font-semibold text-gray-800" htmlFor="new-message">
            New conversation
          </label>
          <div className="flex gap-3">
            <textarea
              id="new-message"
              value={content}
              onChange={(event) => setContent(event.target.value)}
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

        <div className="grid gap-6 lg:grid-cols-[22rem_1fr]">
          <ConversationList
            conversations={conversations}
            activeId={selected?.id}
            onSelect={(conversation) => {
              setSelected({ ...conversation, unread: false });
              setConversations((current) => current.map((item) => (
                item.id === conversation.id ? { ...item, unread: false } : item
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
            showStatus={false}
          />
        </div>
      </div>
    </div>
  );
}
