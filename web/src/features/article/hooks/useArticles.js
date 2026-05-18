import { useCallback, useEffect, useState } from 'react';
import { getArticleCards, getPublicArticleCards } from '../api/articleApi';

export const useArticles = (isLoggedIn = false) => {
  const [articles, setArticles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchArticles = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = isLoggedIn ? await getArticleCards() : await getPublicArticleCards();
      setArticles(data);
    } catch (err) {
      setError(err.message || 'Failed to fetch blogs');
    } finally {
      setLoading(false);
    }
  }, [isLoggedIn]);

  useEffect(() => {
    fetchArticles();
  }, [fetchArticles]);

  return { articles, loading, error, fetchArticles, setArticles };
};
