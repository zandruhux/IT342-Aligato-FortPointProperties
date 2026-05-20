import { useCallback, useEffect, useState } from 'react';
import { getArticleCards } from '../api/articleApi';

export const useArticles = () => {
  const [articles, setArticles] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchArticles = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = await getArticleCards();
      setArticles(data);
    } catch (err) {
      setError(err.message || 'Failed to fetch blogs');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchArticles();
  }, [fetchArticles]);

  return { articles, loading, error, fetchArticles, setArticles };
};
