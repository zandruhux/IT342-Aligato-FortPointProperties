import { FiArrowRight, FiCalendar, FiUser } from 'react-icons/fi';
import ArticleAdminActions from './ArticleAdminActions';

const formatDate = (dateValue) => {
  if (!dateValue) {
    return 'No date';
  }
  return new Intl.DateTimeFormat('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  }).format(new Date(dateValue));
};

export default function ArticleCard({ article, onReadMore, isAdmin = false, onEdit, onDelete, deleting = false }) {
  return (
    <article className="overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm transition hover:-translate-y-1 hover:shadow-md">
      <img
        src={article.coverPhotoUrl}
        alt={article.title}
        className="h-44 w-full object-cover"
      />

      <div className="flex min-h-60 flex-col p-4">
        <h3 className="text-xl font-bold leading-snug text-slate-900">{article.title}</h3>
        <p
          className="mt-3 flex-1 overflow-hidden text-sm leading-6 text-slate-600"
          style={{ display: '-webkit-box', WebkitLineClamp: 3, WebkitBoxOrient: 'vertical' }}
        >
          {article.shortDescription}
        </p>

        <div className="mt-4 space-y-2 border-t border-slate-100 pt-3 text-sm text-slate-500">
          <div className="flex items-center gap-2">
            <FiUser size={16} />
            <span>{article.authorName}</span>
          </div>
          <div className="flex items-center gap-2">
            <FiCalendar size={16} />
            <span>{formatDate(article.latestDate)}</span>
          </div>
        </div>

        <div className="mt-4 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <button
            type="button"
            onClick={() => onReadMore(article)}
            className="inline-flex items-center justify-center gap-2 rounded-md bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700"
          >
            Read More
            <FiArrowRight size={16} />
          </button>
          {isAdmin && (
            <ArticleAdminActions
              onEdit={() => onEdit(article)}
              onDelete={() => onDelete(article)}
              deleting={deleting}
            />
          )}
        </div>
      </div>
    </article>
  );
}
