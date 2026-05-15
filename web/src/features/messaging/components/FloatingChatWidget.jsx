import React, { useCallback, useEffect, useState } from 'react';
import { FiMessageCircle, FiX } from 'react-icons/fi';
import { useAuthContext } from '../../../shared/context/useAuthContext';
import { createConversation, getConversations } from '../api/messagingApi';
import { useMessagingSocket } from '../hooks/useMessagingSocket';
import ConversationPage from '../pages/ConversationPage';

const withDisplayName = (conversation) => ({
  ...conversation,
  displayName: conversation.assignedAgentName
    || (conversation.latestMessageSenderId === conversation.assignedAgentId ? conversation.latestMessageSenderName : '')
    || 'Fort Point Properties',
});

export default function FloatingChatWidget() {
  const { isLoggedIn, user, isRegisteredUser } = useAuthContext();
  const [open, setOpen] = useState(false);
  const [conversations, setConversations] = useState([]);
  const [selected, setSelected] = useState(null);
  const [content, setContent] = useState('');
  const [error, setError] = useState('');

  const canShow = isLoggedIn && isRegisteredUser();

  const loadConversations = useCallback(async () => {
    if (!canShow) {
      return;
    }

    try {
      const data = await getConversations();
      const displayData = data.map(withDisplayName);
      setConversations(displayData);
      setSelected((current) => current || displayData[0] || null);
      setError('');
    } catch (err) {
      setError(err?.error || err?.message || 'Unable to load messages');
    }
  }, [canShow]);

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
    updateConversationPreview(event, !open || selected?.id !== event.conversationId);
  }, [open, selected?.id, updateConversationPreview]);

  useMessagingSocket({
    user,
    role: user?.role,
    onMessage: handleSocketMessage,
  });

  useEffect(() => {
    if (open) {
      loadConversations();
    }
  }, [loadConversations, open]);

  if (!canShow) {
    return null;
  }

  const startConversation = async (event) => {
    event.preventDefault();
    const trimmed = content.trim();
    if (!trimmed) {
      return;
    }

    try {
      const conversation = withDisplayName(await createConversation({ content: trimmed }));
      setConversations((current) => [conversation, ...current]);
      setSelected(conversation);
      setContent('');
      setError('');
    } catch (err) {
      setError(err?.error || err?.message || 'Unable to send message');
    }
  };

  const unreadCount = conversations.filter((conversation) => conversation.unread).length;

  return (
    <div className="fixed bottom-5 right-5 z-50">
      {open && (
        <div className="mb-4 w-[22rem] max-w-[calc(100vw-2.5rem)] overflow-hidden rounded-lg border border-gray-200 bg-white shadow-2xl">
          <div className="flex items-center justify-between bg-blue-600 px-4 py-3 text-white">
            <div>
              <p className="text-sm font-semibold">Fort Point Properties</p>
              <p className="text-xs text-blue-100">Messages</p>
            </div>
            <button
              type="button"
              title="Close chat"
              onClick={() => setOpen(false)}
              className="flex h-8 w-8 items-center justify-center rounded hover:bg-blue-700"
            >
              <FiX aria-hidden="true" />
            </button>
          </div>

          {error && <div className="border-b border-red-200 bg-red-50 px-4 py-2 text-xs text-red-700">{error}</div>}

          {selected ? (
            <ConversationPage
              conversation={selected}
              user={user}
              role={user?.role}
              showStatus={false}
              compact
              onConversationLocked={loadConversations}
              onMessageSent={(message) => updateConversationPreview(message, false)}
            />
          ) : (
            <form onSubmit={startConversation} className="p-4">
              <label htmlFor="floating-message" className="mb-2 block text-sm font-semibold text-gray-800">
                Send us a message
              </label>
              <textarea
                id="floating-message"
                value={content}
                onChange={(event) => setContent(event.target.value)}
                rows={4}
                placeholder="Ask about a property or schedule"
                className="w-full resize-none rounded border border-gray-300 px-3 py-2 text-sm focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-100"
              />
              <button
                type="submit"
                disabled={!content.trim()}
                className="mt-3 w-full rounded bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700 disabled:bg-gray-300"
              >
                Send
              </button>
            </form>
          )}
        </div>
      )}

      <button
        type="button"
        title="Open messages"
        onClick={() => setOpen((current) => !current)}
        className="relative flex h-14 w-14 items-center justify-center rounded-full bg-blue-600 text-white shadow-xl transition hover:bg-blue-700"
      >
        <FiMessageCircle aria-hidden="true" className="text-2xl" />
        {unreadCount > 0 && (
          <span className="absolute -right-1 -top-1 flex h-5 min-w-5 items-center justify-center rounded-full bg-red-600 px-1 text-xs font-bold text-white">
            {unreadCount}
          </span>
        )}
      </button>
    </div>
  );
}
