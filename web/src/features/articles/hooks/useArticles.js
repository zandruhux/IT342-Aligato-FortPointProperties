import { useState, useCallback } from 'react';

/**
 * useArticles Hook
 * Placeholder for articles state management
 * NOTE: Implementation pending backend articles API
 */
export const useArticles = () => {
  const [articles, setArticles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchArticles = useCallback(async () => {
    setError('Articles feature not yet implemented. Awaiting backend API endpoints.');
  }, []);

  const createArticle = useCallback(async (articleData) => {
    setError('Articles feature not yet implemented. Awaiting backend API endpoints.');
  }, []);

  const updateArticle = useCallback(async (id, articleData) => {
    setError('Articles feature not yet implemented. Awaiting backend API endpoints.');
  }, []);

  const deleteArticle = useCallback(async (id) => {
    setError('Articles feature not yet implemented. Awaiting backend API endpoints.');
  }, []);

  return {
    articles,
    loading,
    error,
    fetchArticles,
    createArticle,
    updateArticle,
    deleteArticle,
  };
};

export default useArticles;
