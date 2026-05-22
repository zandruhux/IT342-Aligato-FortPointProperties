import { useState } from 'react';
import { createArticle, deleteArticle, updateArticle } from '../api';

export const useArticleActions = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const runAction = async (action, successMessage) => {
    setLoading(true);
    setError('');
    setSuccess('');
    try {
      const result = await action();
      setSuccess(successMessage);
      return result;
    } catch (err) {
      setError(err.message || 'Blog action failed');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const handleCreateArticle = (formData) => runAction(
    () => createArticle(formData),
    'Blog created successfully.'
  );

  const handleUpdateArticle = (id, formData) => runAction(
    () => updateArticle(id, formData),
    'Blog updated successfully.'
  );

  const handleDeleteArticle = (id) => runAction(
    () => deleteArticle(id),
    'Blog deleted successfully.'
  );

  return {
    loading,
    error,
    success,
    setError,
    setSuccess,
    createArticle: handleCreateArticle,
    updateArticle: handleUpdateArticle,
    deleteArticle: handleDeleteArticle,
  };
};
