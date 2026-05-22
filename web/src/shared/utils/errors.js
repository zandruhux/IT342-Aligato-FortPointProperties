export const getErrorMessage = (error, fallback = 'Something went wrong') => {
  if (!error) return fallback;
  if (typeof error === 'string') return error;

  const responseError = error.response?.data?.error;
  if (typeof responseError === 'string') return responseError;
  if (responseError?.message) return responseError.message;

  if (error.response?.data?.message) return error.response.data.message;
  if (error.message === 'Network Error') {
    return 'Unable to reach the server. Please check your connection or try a smaller file.';
  }
  if (error.message) return error.message;

  return fallback;
};

export const getErrorDetails = (error) => {
  const details = error?.response?.data?.error?.details || error?.details;
  return details && typeof details === 'object' ? details : null;
};

export const normalizeApiError = (error, fallback = 'Something went wrong') => {
  if (error instanceof Error && !error.response && error.message !== 'Network Error') {
    return error;
  }

  const normalized = new Error(getErrorMessage(error, fallback));
  const responseError = error?.response?.data?.error;

  normalized.code = responseError?.code || error?.code || null;
  normalized.status = error?.response?.status || error?.status || null;
  normalized.details = getErrorDetails(error);
  normalized.originalError = error;

  return normalized;
};

export const formatFieldErrors = (details) => {
  if (!details || typeof details !== 'object') return '';
  return Object.values(details).filter(Boolean).join('\n');
};
