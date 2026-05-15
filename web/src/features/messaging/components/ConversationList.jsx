import React from 'react';
import ConversationStatusBadge from './ConversationStatusBadge';

const firstName = (name) => (name || '').trim().split(/\s+/)[0] || '';

const formatPreview = (conversation, currentUser) => {
  const preview = conversation.latestMessagePreview || conversation.preview;
  if (!preview) {
    return 'No messages yet';
  }

  if (conversation.latestMessageSenderId === currentUser?.id) {
    return `You: ${preview}`;
  }

  const senderName = conversation.latestMessageSenderName
    || (conversation.latestMessageSenderId === conversation.registeredUserId
      ? conversation.registeredUserName
      : conversation.assignedAgentName);
  const senderFirstName = firstName(senderName);

  return senderFirstName ? `${senderFirstName}: ${preview}` : preview;
};

export default function ConversationList({ conversations, activeId, onSelect, emptyText, showStatus = true, currentUser }) {
  if (!conversations?.length) {
    return (
      <div className="rounded border border-dashed border-gray-300 bg-white p-8 text-center text-sm text-gray-500">
        {emptyText}
      </div>
    );
  }

  return (
    <div className="divide-y divide-gray-200 rounded border border-gray-200 bg-white">
      {conversations.map((conversation) => (
        <button
          key={conversation.id}
          type="button"
          onClick={() => onSelect(conversation)}
          className={`w-full px-4 py-4 text-left transition ${
            activeId === conversation.id ? 'bg-blue-50' : 'hover:bg-gray-50'
          }`}
        >
          <div className="flex items-start justify-between gap-3">
            <div className="min-w-0">
              <p className={`truncate text-sm ${conversation.unread ? 'font-bold text-gray-950' : 'font-semibold text-gray-900'}`}>
                {conversation.displayName || conversation.registeredUserName || conversation.assignedAgentName || 'Fort Point Properties'}
              </p>
              <p className={`mt-1 truncate text-xs ${conversation.unread ? 'font-bold text-gray-800' : 'text-gray-500'}`}>
                {formatPreview(conversation, currentUser)}
              </p>
            </div>
            {showStatus && <ConversationStatusBadge status={conversation.status} />}
          </div>
        </button>
      ))}
    </div>
  );
}
