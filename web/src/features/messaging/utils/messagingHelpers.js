import { ROLES } from '../../../shared/utils/constants';

// Keep the old misspelling as an alias so stale local storage does not hide messaging UI.
const REGISTERED_ROLE_ALIASES = new Set(['REGISTERED_USER', 'REGISTERED_USEER', ROLES.USER]);

export const normalizeMessagingRole = (role) => {
  const normalized = String(role || '').trim().toUpperCase().replace(/[-\s]+/g, '_');
  return REGISTERED_ROLE_ALIASES.has(normalized) ? ROLES.REGISTERED_USER : normalized;
};

export const withDisplayName = (conversation, isAgentRole = false) => ({
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

export const mergeConversationEvent = (conversation, event, unread, isAgentRole = false) => withDisplayName({
  ...conversation,
  assignedAgentName: event.senderRole === ROLES.AGENT
    ? event.senderName || conversation.assignedAgentName
    : conversation.assignedAgentName,
  assignedAgentProfileImageUrl: event.senderRole === ROLES.AGENT
    ? event.senderProfileImageUrl || conversation.assignedAgentProfileImageUrl
    : conversation.assignedAgentProfileImageUrl,
  registeredUserName: event.senderRole === ROLES.REGISTERED_USER
    ? event.senderName || conversation.registeredUserName
    : conversation.registeredUserName,
  registeredUserProfileImageUrl: event.senderRole === ROLES.REGISTERED_USER
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

export const toInboxConversation = (event) => withDisplayName({
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
  latestMessageAt: event.createdAt || new Date().toISOString(),
  unread: true,
  unreadCount: event.unreadCount || 1,
}, true);

export const applyLockEvent = (conversation, event, isAgentRole = true) => withDisplayName({
  ...conversation,
  assignedAgentId: event.assignedAgentId,
  assignedAgentName: event.assignedAgentName || conversation.assignedAgentName,
  assignedAgentProfileImageUrl: event.assignedAgentProfileImageUrl || conversation.assignedAgentProfileImageUrl,
  status: event.status || 'ASSIGNED',
}, isAgentRole);
