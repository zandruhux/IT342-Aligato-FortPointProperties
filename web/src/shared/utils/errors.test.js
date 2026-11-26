import { describe, expect, it } from 'vitest';
import { formatFieldErrors, getErrorMessage, normalizeApiError } from './errors';

const axiosError = (status, error) => ({
  message: 'Request failed',
  response: { status, data: { success: false, error } },
});

describe('getErrorMessage', () => {
  it('returns the fallback for empty input', () => {
    expect(getErrorMessage(null, 'fallback')).toBe('fallback');
  });

  it('reads the backend ErrorDetail message', () => {
    const error = axiosError(400, { code: 'VAL-001', message: 'Validation failed' });
    expect(getErrorMessage(error)).toBe('Validation failed');
  });

  it('maps axios network errors to a friendly message', () => {
    expect(getErrorMessage({ message: 'Network Error' })).toMatch(/unable to reach the server/i);
  });
});

describe('normalizeApiError', () => {
  it('preserves code, status and field details from the API envelope', () => {
    const error = axiosError(400, {
      code: 'VAL-001',
      message: 'Validation failed',
      details: { email: 'must be a well-formed email address' },
    });

    const normalized = normalizeApiError(error);

    expect(normalized).toBeInstanceOf(Error);
    expect(normalized.code).toBe('VAL-001');
    expect(normalized.status).toBe(400);
    expect(normalized.details).toEqual({ email: 'must be a well-formed email address' });
  });

  it('returns plain Errors untouched', () => {
    const original = new Error('boom');
    expect(normalizeApiError(original)).toBe(original);
  });
});

describe('formatFieldErrors', () => {
  it('joins field messages with newlines', () => {
    expect(formatFieldErrors({ a: 'first', b: 'second', c: '' })).toBe('first\nsecond');
  });
});
