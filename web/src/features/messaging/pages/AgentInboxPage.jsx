import React, { useCallback, useEffect, useState } from 'react';
import { AgentSidebar } from '../../../shared/components/layout';
import { useAuthContext } from '../../../shared/context/useAuthContext';
import { getConversations } from '../api/messagingApi';
import { ConversationList } from '../components';
import { useMessagingSocket } from '../hooks/useMessagingSocket';
import ConversationPage from './ConversationPage';

const withDisplayName = (conversation) => ({
  ...conversation,
  displayName: conversation.registeredUserName
    || (conversation.latestMessageSenderId === conversation.registeredUserId ? conversation.latestMessageSenderName : '')
    || 'Conversation',
});

export default function AgentInboxPage() {
  const { user } = useAuthContext();
  const [conversations, setConversations] = useState([]);
  const [selected, setSelected] = useState(null);
  const [error, setError] = useState('');

  const loadConversations = useCallback(async () => {
    try {
      const data = await getConversations();
      const displayData = data.map(withDisplayName);
      setConversations(displayData);
      setSelected((current) => {
        if (!current) {
          return displayData[0] || null;
        }
        return displayData.find((conversation) => conversation.id === current.id) || current;
      });
      setError('');
    } catch (err) {
      setError(err?.error || err?.message || 'Unable to load agent inbox');
    }
  }, []);

  const handleInboxEvent = useCallback((event) => {
    const incomingConversation = withDisplayName({
      id: event.conversationId,
      registeredUserId: event.registeredUserId,
      registeredUserName: event.registeredUserName,
      assignedAgentId: null,
      assignedAgentName: null,
      status: event.status || 'OPEN',
      latestMessagePreview: event.preview,
      latestMessageSenderId: event.registeredUserId,
      latestMessageSenderName: event.registeredUserName,
      latestMessageAt: event.createdAt || new Date().toISOString(),
      unread: true,
    });

    setConversations((current) => {
      const exists = current.some((conversation) => conversation.id === incomingConversation.id);
      if (exists) {
        return current.map((conversation) => (
          conversation.id === incomingConversation.id
            ? { ...conversation, ...incomingConversation }
            : conversation
        ));
      }
      return [incomingConversation, ...current];
    });

    setSelected((current) => current || incomingConversation);
  }, []);

  const handleLocked = useCallback((event) => {
    setConversations((current) => current.map((conversation) => (
      conversation.id === event.conversationId
        ? { ...conversation, assignedAgentId: event.assignedAgentId, status: event.status }
        : conversation
    )));
    setSelected((current) => (
      current?.id === event.conversationId
        ? { ...current, assignedAgentId: event.assignedAgentId, status: event.status }
        : current
    ));
  }, []);

  const handleMessage = useCallback((event) => {
    setConversations((current) => current.map((conversation) => (
      conversation.id === event.conversationId
        ? {
            ...conversation,
            registeredUserName: event.senderRole === 'REGISTERED_USER'
              ? event.senderName || conversation.registeredUserName
              : conversation.registeredUserName,
            assignedAgentName: event.senderRole === 'AGENT'
              ? event.senderName || conversation.assignedAgentName
              : conversation.assignedAgentName,
            displayName: event.senderRole === 'REGISTERED_USER'
              ? event.senderName || conversation.displayName
              : conversation.displayName,
            latestMessagePreview: event.content,
            latestMessageSenderId: event.senderId,
            latestMessageSenderName: event.senderName,
            latestMessageAt: event.createdAt,
            unread: selected?.id !== event.conversationId,
          }
        : conversation
    )));
    setSelected((current) => (
      current?.id === event.conversationId
        ? {
            ...current,
            registeredUserName: event.senderRole === 'REGISTERED_USER'
              ? event.senderName || current.registeredUserName
              : current.registeredUserName,
            assignedAgentName: event.senderRole === 'AGENT'
              ? event.senderName || current.assignedAgentName
              : current.assignedAgentName,
          }
        : current
    ));
  }, [selected?.id]);

  useMessagingSocket({
    user,
    role: user?.role,
    onInboxEvent: handleInboxEvent,
    onLockedEvent: handleLocked,
    onMessage: handleMessage,
  });

  useEffect(() => {
    loadConversations();
  }, [loadConversations]);

  return (
    <div className="flex h-full">
      <AgentSidebar />
      <div className="ml-56 min-h-screen flex-1 bg-gray-50 py-10">
        <div className="mx-auto max-w-7xl px-4">
          <div className="mb-8">
            <h1 className="text-3xl font-bold text-gray-900">Messages</h1>
            <p className="mt-2 text-gray-600">Open conversations and conversations assigned to you.</p>
          </div>

          {error && <div className="mb-6 rounded border border-red-200 bg-red-50 p-4 text-sm text-red-700">{error}</div>}

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
              emptyText="No conversations available"
              currentUser={user}
            />
            <ConversationPage
              conversation={selected}
              user={user}
              role={user?.role}
              onConversationLocked={handleLocked}
              onMessageSent={loadConversations}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
