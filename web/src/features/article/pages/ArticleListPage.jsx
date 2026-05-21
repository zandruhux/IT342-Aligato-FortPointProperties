import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { FiPlus } from 'react-icons/fi';
import { useAuthContext } from '../../../shared/context/useAuthContext';
import { AdminSidebar, AgentSidebar } from '../../../shared/components/layout';
import ArticleList from '../components/ArticleList';
import PublicArticlePrompt from '../components/PublicArticlePrompt';
import { useArticleActions } from '../hooks/useArticleActions';
import { useArticles } from '../hooks/useArticles';

const normalizeRole = (role) => {
  if (role === 'registered_user' || role === 'USER') {
    return 'REGISTERED_USER';
  }
  return role || '';
};

export default function ArticleListPage() {
  const navigate = useNavigate();
  const { isLoggedIn, user } = useAuthContext();
  const [showPrompt, setShowPrompt] = useState(false);
  const [deletingId, setDeletingId] = useState('');
  const { articles, setArticles, loading, error } = useArticles();
  const articleActions = useArticleActions();

  const role = normalizeRole(user?.role);
  const isAdmin = role === 'ADMIN';
  const usesRoleSidebar = role === 'ADMIN' || role === 'AGENT';

  const handleReadMore = (article) => {
    if (!isLoggedIn) {
      setShowPrompt(true);
      return;
    }
    navigate(`/blogs/${article.id}`);
  };

  const handleDelete = async (article) => {
    const confirmed = window.confirm(`Delete "${article.title}"?`);
    if (!confirmed) {
      return;
    }

    setDeletingId(article.id);
    try {
      await articleActions.deleteArticle(article.id);
      setArticles((currentArticles) => currentArticles.filter((item) => item.id !== article.id));
    } finally {
      setDeletingId('');
    }
  };

  const pageContent = (
    <div className="bg-slate-50 py-12">
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="mb-8 flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <h1 className="text-4xl font-bold text-slate-900">Blogs</h1>
            <p className="mt-2 text-lg text-slate-600">
              News, guides, and property insights from Fort Point Properties.
            </p>
          </div>

          {isAdmin && (
            <button
              type="button"
              onClick={() => navigate('/admin/blogs/create')}
              className="inline-flex items-center justify-center gap-2 rounded-md bg-blue-600 px-5 py-3 font-semibold text-white hover:bg-blue-700"
            >
              <FiPlus size={18} />
              New Blog
            </button>
          )}
        </div>

        {articleActions.error && (
          <div className="mb-5 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
            {articleActions.error}
          </div>
        )}
        {articleActions.success && (
          <div className="mb-5 rounded-md border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700">
            {articleActions.success}
          </div>
        )}

        <ArticleList
          articles={articles}
          loading={loading}
          error={error}
          isAdmin={isAdmin}
          onReadMore={handleReadMore}
          onEdit={(article) => navigate(`/admin/blogs/${article.id}/edit`)}
          onDelete={handleDelete}
          deletingId={deletingId}
        />
      </div>

      <PublicArticlePrompt
        open={showPrompt}
        onClose={() => setShowPrompt(false)}
        title="Log in to read this blog"
        message="Full blog articles are available after you log in or create an account."
      />
    </div>
  );

  if (!usesRoleSidebar) {
    return pageContent;
  }

  return (
    <div className="flex min-h-screen">
      {role === 'ADMIN' ? <AdminSidebar /> : <AgentSidebar />}
      <main className={role === 'ADMIN' ? 'ml-64 flex-1' : 'ml-56 flex-1'}>
        {pageContent}
      </main>
    </div>
  );
}
