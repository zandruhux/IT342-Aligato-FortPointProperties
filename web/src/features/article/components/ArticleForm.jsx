import { useState } from 'react';
import { FiImage, FiSave } from 'react-icons/fi';

export default function ArticleForm({ mode = 'create', initialArticle, loading, error, success, onSubmit }) {
  const [title, setTitle] = useState(initialArticle?.title || '');
  const [description, setDescription] = useState(initialArticle?.description || '');
  const [coverPhoto, setCoverPhoto] = useState(null);
  const [coverPreview, setCoverPreview] = useState(initialArticle?.coverPhotoUrl || '');
  const [validationError, setValidationError] = useState('');

  const isEditMode = mode === 'edit';

  const handleFileChange = (event) => {
    const file = event.target.files?.[0];
    setValidationError('');

    if (!file) {
      setCoverPhoto(null);
      return;
    }

    if (!file.type.startsWith('image/')) {
      setCoverPhoto(null);
      setValidationError('Cover photo must be an image file.');
      return;
    }

    setCoverPhoto(file);
    setCoverPreview(URL.createObjectURL(file));
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    setValidationError('');

    if (!title.trim()) {
      setValidationError('Title is required.');
      return;
    }
    if (!description.trim()) {
      setValidationError('Description is required.');
      return;
    }
    if (!isEditMode && !coverPhoto) {
      setValidationError('Cover photo is required.');
      return;
    }
    if (coverPhoto && !coverPhoto.type.startsWith('image/')) {
      setValidationError('Cover photo must be an image file.');
      return;
    }

    const formData = new FormData();
    formData.append('title', title.trim());
    formData.append('description', description.trim());
    if (coverPhoto) {
      formData.append('coverPhoto', coverPhoto);
    }

    onSubmit(formData);
  };

  return (
    <form onSubmit={handleSubmit} className="rounded-lg border border-slate-200 bg-white p-6 shadow-sm">
      <div className="grid grid-cols-1 gap-6 lg:grid-cols-[1fr_320px]">
        <div className="space-y-5">
          <div>
            <label className="mb-2 block text-sm font-semibold text-slate-700" htmlFor="article-title">
              Title
            </label>
            <input
              id="article-title"
              type="text"
              value={title}
              onChange={(event) => setTitle(event.target.value)}
              className="w-full rounded-md border border-slate-300 px-4 py-3 text-slate-900 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-100"
              placeholder="Enter blog title"
            />
          </div>

          <div>
            <label className="mb-2 block text-sm font-semibold text-slate-700" htmlFor="article-description">
              Description
            </label>
            <textarea
              id="article-description"
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              rows={14}
              className="w-full resize-y rounded-md border border-slate-300 px-4 py-3 text-slate-900 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-100"
              placeholder="Write the full blog content"
            />
          </div>
        </div>

        <div className="space-y-4">
          <div className="overflow-hidden rounded-lg border border-slate-200 bg-slate-50">
            {coverPreview ? (
              <img src={coverPreview} alt="Article cover preview" className="h-56 w-full object-cover" />
            ) : (
              <div className="flex h-56 flex-col items-center justify-center gap-3 text-slate-400">
                <FiImage size={36} />
                <span className="text-sm font-medium">Cover photo preview</span>
              </div>
            )}
          </div>

          <div>
            <label className="mb-2 block text-sm font-semibold text-slate-700" htmlFor="cover-photo">
              Cover Photo {isEditMode ? '(optional)' : ''}
            </label>
            <input
              id="cover-photo"
              type="file"
              accept="image/*"
              onChange={handleFileChange}
              className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm text-slate-700 file:mr-3 file:rounded-md file:border-0 file:bg-blue-50 file:px-3 file:py-2 file:font-semibold file:text-blue-700"
            />
          </div>
        </div>
      </div>

      {(validationError || error) && (
        <div className="mt-5 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {validationError || error}
        </div>
      )}
      {success && (
        <div className="mt-5 rounded-md border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700">
          {success}
        </div>
      )}

      <div className="mt-6 flex justify-end">
        <button
          type="submit"
          disabled={loading}
          className="inline-flex items-center gap-2 rounded-md bg-blue-600 px-5 py-3 font-semibold text-white hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-60"
        >
          <FiSave size={18} />
          {loading ? 'Saving...' : isEditMode ? 'Update Blog' : 'Create Blog'}
        </button>
      </div>
    </form>
  );
}
