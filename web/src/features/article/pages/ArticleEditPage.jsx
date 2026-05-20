import { useNavigate, useParams } from 'react-router-dom';
import { FiArrowLeft } from 'react-icons/fi';
import { AdminSidebar } from '../../../shared/components/layout';
import ArticleForm from '../components/ArticleForm';
import { useArticleActions } from '../hooks/useArticleActions';
import { useArticleDetails } from '../hooks/useArticleDetails';

export default function ArticleEditPage() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { article, loading: loadingArticle, error: articleError } = useArticleDetails(id);
  const { loading, error, success, updateArticle } = useArticleActions();

  const handleSubmit = async (formData) => {
    const updatedArticle = await updateArticle(id, formData);
    setTimeout(() => navigate(`/blogs/${updatedArticle.id}`), 500);
  };

  return (
    <div className="flex min-h-screen">
      <AdminSidebar />
      <main className="ml-64 flex-1 bg-slate-50 py-10">
        <div className="mx-auto max-w-5xl px-6">
          <button
            type="button"
            onClick={() => navigate('/blogs')}
            className="mb-6 inline-flex items-center gap-2 rounded-md border border-slate-300 bg-white px-4 py-2 font-semibold text-slate-700 hover:bg-slate-100"
          >
            <FiArrowLeft size={18} />
            Back
          </button>
          <div className="mb-8">
            <h1 className="text-4xl font-bold text-slate-900">Edit Blog</h1>
            <p className="mt-2 text-slate-600">Update article content or replace the cover photo.</p>
          </div>

          {loadingArticle ? (
            <div className="rounded-lg border border-slate-200 bg-white py-16 text-center text-slate-500">
              Loading blog...
            </div>
          ) : articleError ? (
            <div className="rounded-lg border border-red-200 bg-red-50 py-10 text-center text-red-700">
              {articleError}
            </div>
          ) : (
            <ArticleForm
              key={article.id}
              mode="edit"
              initialArticle={article}
              loading={loading}
              error={error}
              success={success}
              onSubmit={handleSubmit}
            />
          )}
        </div>
      </main>
    </div>
  );
}
