import React, { useCallback, useEffect, useState } from 'react';
import { FiMessageCircle, FiX } from 'react-icons/fi';
import { useLocation } from 'react-router-dom';
import { useAuthContext } from '../../../shared/context/useAuthContext';
import { createConversation, getConversations } from '../api/messagingApi';
import { useMessagingSocket } from '../hooks/useMessagingSocket';
import ConversationPage from '../pages/ConversationPage';

const withDisplayName = (conversation, isAgentRole = false) => ({
  ...conversation,
  displayName: isAgentRole
    ? conversation.registeredUserName
      || (conversation.latestMessageSenderId === conversation.registeredUserId ? conversation.latestMessageSenderName : '')
      || 'Conversation'
    : conversation.assignedAgentName
      || (conversation.latestMessageSenderId === conversation.assignedAgentId ? conversation.latestMessageSenderName : '')
      || 'Fort Point Properties',
  displayProfileImageUrl: isAgentRole
    ? conversation.registeredUserProfileImageUrl
      || (conversation.latestMessageSenderId === conversation.registeredUserId ? conversation.latestMessageSenderProfileImageUrl : null)
    : conversation.assignedAgentProfileImageUrl
      || (conversation.latestMessageSenderId === conversation.assignedAgentId ? conversation.latestMessageSenderProfileImageUrl : null),
});

const mergeConversationEvent = (conversation, event, unread, isAgentRole) => withDisplayName({
  ...conversation,
  assignedAgentName: event.senderRole === 'AGENT'
    ? event.senderName || conversation.assignedAgentName
    : conversation.assignedAgentName,
  assignedAgentProfileImageUrl: event.senderRole === 'AGENT'
    ? event.senderProfileImageUrl || conversation.assignedAgentProfileImageUrl
    : conversation.assignedAgentProfileImageUrl,
  registeredUserName: event.senderRole === 'REGISTERED_USER'
    ? event.senderName || conversation.registeredUserName
    : conversation.registeredUserName,
  registeredUserProfileImageUrl: event.senderRole === 'REGISTERED_USER'
    ? event.senderProfileImageUrl || conversation.registeredUserProfileImageUrl
    : conversation.registeredUserProfileImageUrl,
  latestMessagePreview: event.content,
  latestMessageSenderId: event.senderId,
  latestMessageSenderName: event.senderName,
  latestMessageSenderProfileImageUrl: event.senderProfileImageUrl,
  latestMessageAt: event.createdAt,
  unread,
  unreadCount: unread ? (conversation.unreadCount || 0) + (event.unreadCount || 1) : 0,
}, isAgentRole);

export default function FloatingChatWidget() {
  const location = useLocation();
  const { isLoggedIn, user, isAgent, isRegisteredUser } = useAuthContext();
  const [open, setOpen] = useState(false);
  const [conversations, setConversations] = useState([]);
  const [selected, setSelected] = useState(null);
  const [incomingMessage, setIncomingMessage] = useState(null);
  const [content, setContent] = useState('');
  const [error, setError] = useState('');

  const isRegisteredUserRole = isRegisteredUser();
  const isAgentRole = isAgent();
  const canShow = isLoggedIn
    && (isRegisteredUserRole || isAgentRole)
    && !location.pathname.startsWith('/messages')
    && !location.pathname.startsWith('/agent/messages');
  const unreadCount = conversations.reduce((total, conversation) => total + (conversation.unreadCount || 0), 0);

  const loadConversations = useCallback(async () => {
    if (!canShow) {
      return;
    }

    try {
      const data = await getConversations();
      const displayData = data.map((conversation) => withDisplayName(conversation, isAgentRole));
      setConversations(displayData);
      setSelected((current) => (isAgentRole ? displayData[0] || null : current || displayData[0] || null));
      setError('');
    } catch (err) {
      setError(err?.error || err?.message || 'Unable to load messages');
    }
  }, [canShow, isAgentRole]);

  const updateConversationPreview = useCallback((event, unread) => {
    setConversations((current) => {
      const updated = current.map((conversation) => (
        conversation.id === event.conversationId
          ? mergeConversationEvent(conversation, event, unread, isAgentRole)
          : conversation
      ));

      if (!isAgentRole) {
        return updated;
      }

      // Agent widgets intentionally surface only the newest conversation.
      const latest = updated.find((conversation) => conversation.id === event.conversationId);
      if (!latest) {
        return updated;
      }

      return [
        latest,
        ...updated.filter((conversation) => conversation.id !== event.conversationId),
      ];
    });
    setSelected((current) => (
      current?.id === event.conversationId
        ? mergeConversationEvent(current, event, unread, isAgentRole)
        : current
    ));
  }, [isAgentRole]);

  const handleSocketMessage = useCallback((event) => {
    updateConversationPreview(event, !open || selected?.id !== event.conversationId);
    if (selected?.id === event.conversationId) {
      setIncomingMessage(event);
    }
  }, [open, selected?.id, updateConversationPreview]);

  useMessagingSocket({
    user: canShow ? user : null,
    role: canShow ? user?.role : null,
    enabled: canShow,
    onInboxEvent: isAgentRole ? (event) => {
      // Inbox events can arrive before the agent opens the full messages page.
      const incomingConversation = withDisplayName({
        id: event.conversationId,
        registeredUserId: event.registeredUserId,
        registeredUserName: event.registeredUserName,
        registeredUserProfileImageUrl: event.registeredUserProfileImageUrl,
        assignedAgentId: null,
        assignedAgentName: null,
        status: event.status || 'OPEN',
        latestMessagePreview: event.preview,
        latestMessageSenderId: event.registeredUserId,
        latestMessageSenderName: event.registeredUserName,
        unread: true,
        unreadCount: event.unreadCount || 1,
      }, true);

      setConversations((current) => {
        if (current.some((conversation) => conversation.id === incomingConversation.id)) {
          const updated = current.map((conversation) => (
            conversation.id === incomingConversation.id
              ? {
                  ...conversation,
                  ...incomingConversation,
                  unreadCount: (conversation.unreadCount || 0) + (event.unreadCount || 1),
                }
              : conversation
          ));
          return [
            updated.find((conversation) => conversation.id === incomingConversation.id),
            ...updated.filter((conversation) => conversation.id !== incomingConversation.id),
          ];
        }
        return [incomingConversation, ...current];
      });
      setSelected(incomingConversation);
    } : undefined,
    onMessage: handleSocketMessage,
  });

  useEffect(() => {
    if (canShow) {
      loadConversations();
    }
  }, [canShow, loadConversations]);

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
      const conversation = withDisplayName(await createConversation({ content: trimmed }), false);
      setConversations((current) => [conversation, ...current]);
      setSelected(conversation);
      setContent('');
      setError('');
    } catch (err) {
      setError(err?.error || err?.message || 'Unable to send message');
    }
  };

  const submitOnEnter = (event) => {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      event.currentTarget.form?.requestSubmit();
    }
  };

  const handleLauncherClick = () => {
    if (isAgentRole) {
      setSelected(conversations[0] || null);
    }
    setOpen(true);
  };

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
              incomingMessage={incomingMessage}
            />
          ) : isRegisteredUserRole ? (
            <form onSubmit={startConversation} className="p-4">
              <label htmlFor="floating-message" className="mb-2 block text-sm font-semibold text-gray-800">
                Send us a message
              </label>
              <textarea
                id="floating-message"
                value={content}
                onChange={(event) => setContent(event.target.value)}
                onKeyDown={submitOnEnter}
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
          ) : (
            <div className="p-6 text-center text-sm text-gray-500">
              No conversations available.
            </div>
          )}
        </div>
      )}

      {!open && (
        <button
          type="button"
          title="Open messages"
          onClick={handleLauncherClick}
          className="relative flex h-14 w-14 items-center justify-center rounded-full bg-blue-600 text-white shadow-xl transition hover:bg-blue-700"
        >
          <FiMessageCircle aria-hidden="true" className="text-2xl" />
          {unreadCount > 0 && (
            <span className="absolute -right-1 -top-1 flex h-5 min-w-5 items-center justify-center rounded-full bg-red-600 px-1 text-xs font-bold text-white">
              {unreadCount}
            </span>
          )}
        </button>
      )}
    </div>
  );
}
