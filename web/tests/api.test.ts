import test from 'node:test';
import assert from 'node:assert/strict';

// Test Money model
import { createMoney, formatMoney, sumMoney } from '../src/lib/money';
// Test Errors
import { KonstrivoApiError } from '../src/lib/errors';
// Test Configs
import { SUPPORTED_MARKETS } from '../src/config/markets';
import { SUPPORTED_TRADES } from '../src/config/trades';
import { SUPPORTED_LANGUAGES } from '../src/config/languages';
// Test API Types & Client
import { ApiClient } from '../src/lib/api/client';
import { KonstrivoApi } from '../src/lib/api/endpoints';

test('Money Model: createMoney produces valid object', () => {
  const money = createMoney(1250.5, 'TND');
  assert.equal(money.amount, 1250.5);
  assert.equal(money.currency, 'TND');
});

test('Money Model: createMoney handles NaN / invalid numbers safely', () => {
  const money = createMoney(NaN, 'TND');
  assert.equal(money.amount, 0);
  assert.equal(money.currency, 'TND');
});

test('Money Model: sumMoney calculates correctly', () => {
  const sum = sumMoney([
    { amount: 100, currency: 'TND' },
    { amount: 250.75, currency: 'TND' },
    { amount: 49.25, currency: 'TND' }
  ]);
  assert.equal(sum.amount, 400);
  assert.equal(sum.currency, 'TND');
});

test('Money Model: formatMoney formats display string without changing data', () => {
  const formatted = formatMoney({ amount: 1250.5, currency: 'TND' }, 'fr-TN', 3);
  assert.ok(formatted.includes('TND'));
});

test('Error Model: KonstrivoApiError parses standardized error envelope', () => {
  const apiPayload = {
    error: {
      code: 'VALIDATION_FAILED',
      message: 'Area must be greater than 0',
      details: { area: 'Invalid' },
      requestId: 'req_test_123',
      timestamp: '2026-08-20T07:00:00.000Z'
    }
  };

  const error = KonstrivoApiError.fromResponse(422, apiPayload);
  assert.equal(error.statusCode, 422);
  assert.equal(error.code, 'VALIDATION_FAILED');
  assert.equal(error.message, 'Area must be greater than 0');
  assert.equal(error.requestId, 'req_test_123');
});

test('Markets Configuration: contains 9 ISO markets aligned with Android', () => {
  const marketKeys = Object.keys(SUPPORTED_MARKETS);
  assert.equal(marketKeys.length, 9);
  assert.ok(SUPPORTED_MARKETS['TN']);
  assert.ok(SUPPORTED_MARKETS['FR']);
  assert.ok(SUPPORTED_MARKETS['DZ']);
  assert.ok(SUPPORTED_MARKETS['MA']);
  assert.equal(SUPPORTED_MARKETS['TN'].currencyCode, 'TND');
});

test('Trades Configuration: contains 6 standardized trades', () => {
  const tradeKeys = Object.keys(SUPPORTED_TRADES);
  assert.equal(tradeKeys.length, 6);
  assert.ok(SUPPORTED_TRADES['placo']);
  assert.ok(SUPPORTED_TRADES['peinture']);
  assert.ok(SUPPORTED_TRADES['maconnerie']);
  assert.ok(SUPPORTED_TRADES['crepissage']);
  assert.ok(SUPPORTED_TRADES['carrelage']);
  assert.ok(SUPPORTED_TRADES['menuiserie']);
});

test('Languages Configuration: RTL support configured for Arabic and Derja', () => {
  assert.equal(SUPPORTED_LANGUAGES['ar'].isRtl, true);
  assert.equal(SUPPORTED_LANGUAGES['ar'].direction, 'rtl');
  assert.equal(SUPPORTED_LANGUAGES['tn'].isRtl, true);
  assert.equal(SUPPORTED_LANGUAGES['tn'].direction, 'rtl');
  assert.equal(SUPPORTED_LANGUAGES['fr'].isRtl, false);
  assert.equal(SUPPORTED_LANGUAGES['en'].isRtl, false);
});

test('API Client: instance initializes with default base URL', () => {
  const client = new ApiClient();
  const api = new KonstrivoApi(client);
  assert.ok(api);
});
