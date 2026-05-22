import { normalizeApiError } from './errors';

const ALLOWED_IMAGE_TYPES = ['image/jpeg', 'image/png', 'image/webp'];
const MB = 1024 * 1024;

export const IMAGE_LIMITS = {
  PROFILE: 2 * MB,
  PROPERTY: 5 * MB,
  BLOG: 5 * MB,
};

export const formatFileSize = (bytes) => `${(bytes / MB).toFixed(2)} MB`;

export const validateImageFile = (file, {
  maxSizeBytes,
  requiredMessage = 'Image is required',
  sizeMessage = 'Image size exceeds the maximum allowed limit',
  typeMessage = 'Invalid image file type',
} = {}) => {
  if (!file) {
    throw normalizeApiError({ message: requiredMessage }, requiredMessage);
  }

  if (!ALLOWED_IMAGE_TYPES.includes(file.type)) {
    throw normalizeApiError({ message: typeMessage }, typeMessage);
  }

  if (maxSizeBytes && file.size > maxSizeBytes) {
    throw normalizeApiError({
      message: `${sizeMessage}. Maximum: ${formatFileSize(maxSizeBytes)}. Selected: ${formatFileSize(file.size)}.`,
    }, sizeMessage);
  }
};
