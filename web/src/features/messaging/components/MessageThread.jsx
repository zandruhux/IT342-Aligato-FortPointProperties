import React, { useEffect, useRef } from 'react';
import ConversationAvatar from './ConversationAvatar';

const nameFromUser = (user) => {
  const fullName = `${user?.firstname || ''} ${user?.lastname || ''}`.trim();
  return fullName || user?.email || '';
};

const resolveSenderName = (message, currentUser, conversation) => {
  if (message.senderName) {
    return message.senderName;
  }
  if (message.senderId === currentUser?.id) {
    return nameFromUser(currentUser);
  }
  if (message.senderRole === 'AGENT') {
    return conversation?.assignedAgentName || 'Sender';
  }
  return conversation?.registeredUserName || 'Sender';
};

const resolveSenderImage = (message, currentUser, conversation) => {
  if (message.senderProfileImageUrl) {
    return message.senderProfileImageUrl;
  }
  if (message.senderId === currentUser?.id) {
    return currentUser?.profileImageUrl;
  }
  if (message.senderRole === 'AGENT') {
    return conversation?.assignedAgentProfileImageUrl;
  }
  return conversation?.registeredUserProfileImageUrl;
};

export default function MessageThread({ messages, currentUser, conversation }) {
  const bottomRef = useRef(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  if (!messages?.length) {
    return <div className="flex min-h-96 items-center justify-center text-sm text-gray-500">No messages yet</div>;
  }

  return (
    <div className="h-[32rem] overflow-y-auto bg-gray-50 p-4">
      <div className="space-y-3">
        {messages.map((message) => {
          const mine = message.senderId === currentUser?.id;
          const senderName = resolveSenderName(message, currentUser, conversation);
          return (
            <div key={message.id || `${message.conversationId}-${message.createdAt}-${message.senderId}`} className={`flex items-end gap-2 ${mine ? 'justify-end' : 'justify-start'}`}>
              {!mine && (
                <ConversationAvatar
                  src={resolveSenderImage(message, currentUser, conversation)}
                  name={senderName}
                  size="sm"
                />
              )}
              <div className={`max-w-[75%] rounded-lg px-4 py-3 shadow-sm ${mine ? 'bg-blue-600 text-white' : 'bg-white text-gray-900 border border-gray-200'}`}>
                <p className="whitespace-pre-wrap break-words text-sm">{message.content}</p>
                <p className={`mt-2 text-[11px] ${mine ? 'text-blue-100' : 'text-gray-500'}`}>
                  {senderName}
                </p>
              </div>
              {mine && (
                <ConversationAvatar
                  src={resolveSenderImage(message, currentUser, conversation)}
                  name={senderName}
                  size="sm"
                />
              )}
            </div>
          );
        })}
        <div ref={bottomRef} />
      </div>
    </div>
  );
}
