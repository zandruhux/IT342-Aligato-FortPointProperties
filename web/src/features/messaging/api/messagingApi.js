import axiosInstance from '../../../shared/utils/api';
import { API_ENDPOINTS } from '../../../shared/utils/constants';

const unwrap = (response) => response.data?.data || response.data;

const withMessagingError = async (request) => {
  try {
    return unwrap(await request());
  } catch (error) {
    throw {
      status: error.response?.status,
      message: error.response?.data?.error
        || error.response?.data?.message
        || (error.response?.status === 403 ? 'Messaging is only available to registered users and agents.' : null)
        || error.message
        || 'Messaging request failed',
    };
  }
};

export const createConversation = async (payload) => {
  return withMessagingError(() => axiosInstance.post(API_ENDPOINTS.MESSAGING.CONVERSATIONS, payload));
};

export const getConversations = async () => {
  return withMessagingError(() => axiosInstance.get(API_ENDPOINTS.MESSAGING.CONVERSATIONS));
};

export const getMessages = async (conversationId) => {
  return withMessagingError(() => axiosInstance.get(API_ENDPOINTS.MESSAGING.MESSAGES(conversationId)));
};

export const sendMessage = async (conversationId, payload) => {
  return withMessagingError(() => axiosInstance.post(API_ENDPOINTS.MESSAGING.MESSAGES(conversationId), payload));
};

export const markConversationRead = async (conversationId) => {
  return withMessagingError(() => axiosInstance.put(API_ENDPOINTS.MESSAGING.READ(conversationId)));
};

export default {
  createConversation,
  getConversations,
  getMessages,
  sendMessage,
  markConversationRead,
};
