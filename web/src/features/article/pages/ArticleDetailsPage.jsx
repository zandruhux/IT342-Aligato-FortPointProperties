import { useNavigate, useParams } from 'react-router-dom';
import { FiArrowLeft, FiCalendar, FiUser } from 'react-icons/fi';
import { useAuthContext } from '../../../shared/context/useAuthContext';
import { AdminSidebar, AgentSidebar } from '../../../shared/components/layout';
import { useArticleDetails } from '../hooks/useArticleDetails';

const formatDate = (dateValue) => {
  if (!dateValue) {
    return 'No date';
  }
  return new Intl.DateTimeFormat('en-US', {
    month: 'long',
    day: 'numeric',
    year: 'numeric',
  }).format(new Date(dateValue));
};

const normalizeRole = (role) => {
  if (role === 'registered_user' || role === 'USER') {
    return 'REGISTERED_USER';
  }
  return role || '';
};

export default function ArticleDetailsPage() {
  const navigate = useNavigate();
  const { id } = useParams();
  const { user } = useAuthContext();
  const { article, loading, error } = useArticleDetails(id);
  const role = normalizeRole(user?.role);
  const usesRoleSidebar = role === 'ADMIN' || role === 'AGENT';

  const withRoleShell = (content) => {
    if (!usesRoleSidebar) {
      return content;
    }

    return (
      <div className="flex min-h-screen">
        {role === 'ADMIN' ? <AdminSidebar /> : <AgentSidebar />}
        <main className={role === 'ADMIN' ? 'ml-64 flex-1' : 'ml-56 flex-1'}>
          {content}
        </main>
      </div>
    );
  };

  if (loading) {
    return withRoleShell(
      <div className="bg-slate-50 py-16 text-center text-slate-500">
        Loading blog details...
      </div>
    );
  }

  if (error) {
    return withRoleShell(
      <div className="bg-slate-50 py-16">
        <div className="mx-auto max-w-6xl px-4">
          <button
            type="button"
            onClick={() => navigate('/blogs')}
            className="mb-6 inline-flex items-center gap-2 rounded-md border border-slate-300 px-4 py-2 font-semibold text-slate-700 hover:bg-white"
          >
            <FiArrowLeft size={18} />
            Back
          </button>
          <div className="rounded-lg border border-red-200 bg-red-50 px-4 py-8 text-center text-red-700">
            {error}
          </div>
        </div>
      </div>
    );
  }

  if (!article) {
    return null;
  }

  return withRoleShell(
    <div className="bg-slate-50 py-10">
      <article className="mx-auto max-w-6xl px-4 sm:px-6 lg:px-8">
        <button
          type="button"
          onClick={() => navigate('/blogs')}
          className="mb-6 inline-flex items-center gap-2 rounded-md border border-slate-300 bg-white px-4 py-2 font-semibold text-slate-700 hover:bg-slate-100"
        >
          <FiArrowLeft size={18} />
          Back
        </button>

        <div className="overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm">
          <div className="p-6 sm:p-8">
            <h1 className="text-4xl font-bold leading-tight text-slate-900 sm:text-5xl">{article.title}</h1>
          </div>

          <img
            src={article.coverPhotoUrl}
            alt={article.title}
            className="h-96 w-full object-cover lg:h-[30rem]"
          />

          <div className="p-6 sm:p-8">
            <div className="flex flex-wrap gap-5 text-sm text-slate-500">
              <span className="inline-flex items-center gap-2">
                <FiUser size={16} />
                {article.authorName}
              </span>
              <span className="inline-flex items-center gap-2">
                <FiCalendar size={16} />
                {formatDate(article.latestDate)}
              </span>
            </div>

            <div className="mt-8 whitespace-pre-wrap text-base leading-8 text-slate-700">
              {article.description}
            </div>
          </div>
        </div>
      </article>
    </div>
  );
}
