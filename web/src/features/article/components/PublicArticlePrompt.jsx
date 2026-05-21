import { Link } from 'react-router-dom';
import { FiAlertCircle, FiX } from 'react-icons/fi';

export default function PublicArticlePrompt({
  open,
  onClose,
  title = 'Login Required',
  message = 'Create an account or log in to continue.',
}) {
  if (!open) {
    return null;
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/45 px-4 backdrop-blur-sm">
      <div className="w-full max-w-md rounded-lg bg-white p-6 shadow-xl">
        <div className="mb-4 flex items-start justify-between gap-4">
          <div className="flex gap-3">
            <div className="mt-1 flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-blue-50 text-blue-600">
              <FiAlertCircle size={22} />
            </div>
            <div>
            <h2 className="text-2xl font-bold text-slate-900">{title}</h2>
            <p className="mt-2 text-slate-600">
              {message}
            </p>
            </div>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="rounded-md p-2 text-slate-500 hover:bg-slate-100"
            aria-label="Close login prompt"
          >
            <FiX size={20} />
          </button>
        </div>

        <div className="flex flex-col gap-3 sm:flex-row">
          <Link
            to="/login"
            className="flex-1 rounded-md bg-blue-600 px-4 py-2 text-center font-semibold text-white no-underline hover:bg-blue-700"
          >
            Login
          </Link>
          <Link
            to="/register"
            className="flex-1 rounded-md border border-slate-300 px-4 py-2 text-center font-semibold text-slate-800 no-underline hover:bg-slate-50"
          >
            Sign Up
          </Link>
        </div>
      </div>
    </div>
  );
}
