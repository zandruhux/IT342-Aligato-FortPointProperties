import { useNavigate } from 'react-router-dom';
import { FiArrowLeft } from 'react-icons/fi';
import { AdminSidebar } from '../../../shared/components/layout';
import ArticleForm from '../components/ArticleForm';
import { useArticleActions } from '../hooks/useArticleActions';

export default function ArticleCreatePage() {
  const navigate = useNavigate();
  const { loading, error, success, createArticle } = useArticleActions();

  const handleSubmit = async (formData) => {
    const createdArticle = await createArticle(formData);
    setTimeout(() => navigate(`/blogs/${createdArticle.id}`), 500);
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
            <h1 className="text-4xl font-bold text-slate-900">Create Blog</h1>
            <p className="mt-2 text-slate-600">Publish a new article for your readers.</p>
          </div>
          <ArticleForm
            mode="create"
            loading={loading}
            error={error}
            success={success}
            onSubmit={handleSubmit}
          />
        </div>
      </main>
    </div>
  );
}
