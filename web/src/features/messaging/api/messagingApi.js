import axiosInstance from '../../../shared/utils/api';
import { API_ENDPOINTS } from '../../../shared/utils/constants';

const unwrap = (response) => response.data?.data || response.data;

export const createConversation = async (payload) => {
  const response = await axiosInstance.post(API_ENDPOINTS.MESSAGING.CONVERSATIONS, payload);
  return unwrap(response);
};

export const getConversations = async () => {
  const response = await axiosInstance.get(API_ENDPOINTS.MESSAGING.CONVERSATIONS);
  return unwrap(response);
};

export const getMessages = async (conversationId) => {
  const response = await axiosInstance.get(API_ENDPOINTS.MESSAGING.MESSAGES(conversationId));
  return unwrap(response);
};

export const sendMessage = async (conversationId, payload) => {
  const response = await axiosInstance.post(API_ENDPOINTS.MESSAGING.MESSAGES(conversationId), payload);
  return unwrap(response);
};

export default {
  createConversation,
  getConversations,
  getMessages,
  sendMessage,
};
