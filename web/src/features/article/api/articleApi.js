import axios from 'axios';
import axiosInstance from '../../../shared/utils/api';
import { API_BASE_URL, API_ENDPOINTS } from '../../../shared/utils/constants';

const publicArticleApi = axios.create({
  baseURL: API_BASE_URL,
});

const extractData = (response) => response.data?.data || response.data || [];

const extractError = (error, fallback) => {
  return error.response?.data?.error?.message || error.response?.data?.message || fallback;
};

export const getArticleCards = async () => {
  try {
    const response = await publicArticleApi.get(API_ENDPOINTS.ARTICLES.ALL);
    return extractData(response);
  } catch (error) {
    throw new Error(extractError(error, 'Failed to fetch blogs'));
  }
};

export const getArticleById = async (id) => {
  try {
    const response = await axiosInstance.get(API_ENDPOINTS.ARTICLES.BY_ID(id));
    return extractData(response);
  } catch (error) {
    throw new Error(extractError(error, 'Failed to fetch blog details'));
  }
};

export const createArticle = async (formData) => {
  try {
    const response = await axiosInstance.post(API_ENDPOINTS.ARTICLES.CREATE, formData);
    return extractData(response);
  } catch (error) {
    throw new Error(extractError(error, 'Failed to create blog'));
  }
};

export const updateArticle = async (id, formData) => {
  try {
    const response = await axiosInstance.put(API_ENDPOINTS.ARTICLES.UPDATE(id), formData);
    return extractData(response);
  } catch (error) {
    throw new Error(extractError(error, 'Failed to update blog'));
  }
};

export const deleteArticle = async (id) => {
  try {
    const response = await axiosInstance.delete(API_ENDPOINTS.ARTICLES.DELETE(id));
    return extractData(response);
  } catch (error) {
    throw new Error(extractError(error, 'Failed to delete blog'));
  }
};
