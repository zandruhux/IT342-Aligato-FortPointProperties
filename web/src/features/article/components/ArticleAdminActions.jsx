import { FiEdit2, FiTrash2 } from 'react-icons/fi';

export default function ArticleAdminActions({ onEdit, onDelete, deleting = false }) {
  return (
    <div className="flex items-center gap-2">
      <button
        type="button"
        onClick={onEdit}
        className="inline-flex items-center gap-2 rounded-md border border-blue-200 px-3 py-2 text-sm font-semibold text-blue-700 hover:bg-blue-50"
      >
        <FiEdit2 size={16} />
        Edit
      </button>
      <button
        type="button"
        onClick={onDelete}
        disabled={deleting}
        className="inline-flex items-center gap-2 rounded-md border border-red-200 px-3 py-2 text-sm font-semibold text-red-700 hover:bg-red-50 disabled:cursor-not-allowed disabled:opacity-60"
      >
        <FiTrash2 size={16} />
        {deleting ? 'Deleting...' : 'Delete'}
      </button>
    </div>
  );
}
