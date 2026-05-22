import { useCallback, useEffect, useState } from 'react';
import { getArticleById } from '../api';

export const useArticleDetails = (id) => {
  const [article, setArticle] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchArticle = useCallback(async () => {
    if (!id) {
      return;
    }

    setLoading(true);
    setError('');
    try {
      const data = await getArticleById(id);
      setArticle(data);
    } catch (err) {
      setError(err.message || 'Failed to fetch blog details');
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchArticle();
  }, [fetchArticle]);

  return { article, loading, error, fetchArticle };
};
