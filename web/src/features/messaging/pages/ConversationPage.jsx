import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { ROLES } from '../../../shared/utils/constants';
import { getMessages, sendMessage } from '../api/messagingApi';
import { MessageInput, MessageThread, ConversationStatusBadge } from '../components';
import { useMessagingSocket } from '../hooks/useMessagingSocket';

const normalizeRole = (role) => {
  if (role === 'registered_user' || role === ROLES.USER) {
    return ROLES.REGISTERED_USER;
  }
  return role;
};

const getDisplayName = (conversation, normalizedRole) => {
  if (!conversation) {
    return '';
  }
  if (normalizedRole === ROLES.AGENT) {
    return conversation.registeredUserName
      || (conversation.latestMessageSenderId === conversation.registeredUserId ? conversation.latestMessageSenderName : '')
      || 'Conversation';
  }
  return conversation.assignedAgentName
    || (conversation.latestMessageSenderId === conversation.assignedAgentId ? conversation.latestMessageSenderName : '')
    || 'Fort Point Properties';
};

export default function ConversationPage({
  conversation,
  user,
  role,
  onConversationLocked,
  onMessageSent,
  showStatus = true,
  compact = false,
}) {
  const [messages, setMessages] = useState([]);
  const [error, setError] = useState('');
  const normalizedRole = normalizeRole(role || user?.role);

  const canSend = useMemo(() => {
    if (!conversation || !user?.id) {
      return false;
    }
    if (normalizedRole === ROLES.REGISTERED_USER) {
      return conversation.registeredUserId === user.id;
    }
    if (normalizedRole === ROLES.AGENT) {
      return !conversation.assignedAgentId || conversation.assignedAgentId === user.id;
    }
    return false;
  }, [conversation, normalizedRole, user?.id]);

  const loadMessages = useCallback(async () => {
    if (!conversation?.id) {
      setMessages([]);
      return;
    }

    if (normalizedRole === ROLES.AGENT && conversation.status === 'OPEN') {
      setMessages([]);
      return;
    }

    try {
      const data = await getMessages(conversation.id);
      setMessages(data);
      setError('');
    } catch (err) {
      setError(err?.error || err?.message || 'Unable to load messages');
    }
  }, [conversation?.id, conversation?.status, normalizedRole]);

  const handleMessage = useCallback((event) => {
    if (event.conversationId === conversation?.id) {
      setMessages((current) => [...current, event]);
    }
  }, [conversation?.id]);

  const handleLocked = useCallback((event) => {
    if (event.conversationId === conversation?.id) {
      onConversationLocked?.(event);
    }
  }, [conversation?.id, onConversationLocked]);

  useMessagingSocket({
    user,
    role: normalizedRole,
    conversationId: conversation?.id,
    onLockedEvent: handleLocked,
    onMessage: handleMessage,
  });

  useEffect(() => {
    loadMessages();
  }, [loadMessages]);

  const submitMessage = async (content) => {
    try {
      const saved = await sendMessage(conversation.id, { content });
      setMessages((current) => {
        if (current.some((message) => message.id === saved.id)) {
          return current;
        }
        return [...current, saved];
      });
      setError('');
      onMessageSent?.(saved);
      if (normalizedRole === ROLES.AGENT && conversation.status === 'OPEN') {
        onConversationLocked?.({
          conversationId: conversation.id,
          assignedAgentId: user.id,
          status: 'ASSIGNED',
        });
        loadMessages();
      }
    } catch (err) {
      setError(err?.error || err?.message || 'Unable to send message');
    }
  };

  if (!conversation) {
    return (
      <div className="flex min-h-[32rem] items-center justify-center rounded border border-dashed border-gray-300 bg-white text-sm text-gray-500">
        Select a conversation
      </div>
    );
  }

  return (
    <section className="overflow-hidden rounded border border-gray-200 bg-white">
      <div className="flex items-center justify-between border-b border-gray-200 px-4 py-3">
        <div>
          <h2 className={`${compact ? 'text-base' : 'text-lg'} font-semibold text-gray-900`}>
            {getDisplayName(conversation, normalizedRole)}
          </h2>
          {normalizedRole === ROLES.AGENT && (
            <p className="text-sm text-gray-500">
              {conversation.assignedAgentId ? 'Assigned conversation' : 'Waiting for an agent'}
            </p>
          )}
        </div>
        {showStatus && <ConversationStatusBadge status={conversation.status} />}
      </div>

      {error && <div className="border-b border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">{error}</div>}

      {normalizedRole === ROLES.AGENT && conversation.status === 'OPEN' ? (
        <div className="flex h-[32rem] items-center justify-center bg-gray-50 px-6 text-center text-sm text-gray-600">
          Reply to assign this conversation to yourself.
        </div>
      ) : (
        <div className={compact ? '[&>div]:h-80' : ''}>
          <MessageThread messages={messages} currentUser={user} conversation={conversation} />
        </div>
      )}

      <MessageInput
        disabled={!canSend}
        placeholder={canSend ? 'Write a message' : 'This conversation is assigned to another agent'}
        onSend={submitMessage}
      />
    </section>
  );
}
