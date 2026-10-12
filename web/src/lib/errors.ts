import { ApiError } from '@/types/api';

export class KonstrivoApiError extends Error {
  public readonly code: string;
  public readonly details?: Record<string, unknown>;
  public readonly requestId: string;
  public readonly timestamp: string;
  public readonly statusCode: number;

  constructor(
    statusCode: number,
    code: string,
    message: string,
    details?: Record<string, unknown>,
    requestId = 'req_unknown',
    timestamp = new Date().toISOString()
  ) {
    super(message);
    this.name = 'KonstrivoApiError';
    this.statusCode = statusCode;
    this.code = code;
    this.details = details;
    this.requestId = requestId;
    this.timestamp = timestamp;
  }

  static fromResponse(statusCode: number, body: unknown): KonstrivoApiError {
    if (
      body &&
      typeof body === 'object' &&
      'error' in body &&
      typeof (body as ApiError).error === 'object'
    ) {
      const err = (body as ApiError).error;
      return new KonstrivoApiError(
        statusCode,
        err.code || 'UNKNOWN_ERROR',
        err.message || 'An unexpected API error occurred',
        err.details,
        err.requestId || `req_${Date.now().toString(36)}`,
        err.timestamp || new Date().toISOString()
      );
    }

    return new KonstrivoApiError(
      statusCode,
      `HTTP_${statusCode}`,
      `Request failed with HTTP status ${statusCode}`,
      undefined,
      `req_${Date.now().toString(36)}`,
      new Date().toISOString()
    );
  }
}
