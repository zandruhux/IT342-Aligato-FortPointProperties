import ArticleCard from './ArticleCard';

export default function ArticleList({
  articles,
  loading,
  error,
  isAdmin,
  onReadMore,
  onEdit,
  onDelete,
  deletingId,
  emptyMessage = 'No blogs have been posted yet.',
}) {
  if (loading) {
    return (
      <div className="rounded-lg border border-slate-200 bg-white py-16 text-center text-slate-500">
        Loading blogs...
      </div>
    );
  }

  if (error) {
    return (
      <div className="rounded-lg border border-red-200 bg-red-50 py-10 text-center text-red-700">
        {error}
      </div>
    );
  }

  if (!articles.length) {
    return (
      <div className="rounded-lg border border-slate-200 bg-white py-16 text-center text-slate-500">
        {emptyMessage}
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 gap-6 md:grid-cols-2 xl:grid-cols-3">
      {articles.map((article) => (
        <ArticleCard
          key={article.id}
          article={article}
          isAdmin={isAdmin}
          onReadMore={onReadMore}
          onEdit={onEdit}
          onDelete={onDelete}
          deleting={deletingId === article.id}
        />
      ))}
    </div>
  );
}
