import test from 'node:test';
import assert from 'node:assert/strict';

import {
  buildAdminCredential,
  parseCredential,
  verifyAdminCredential,
} from '../src/lib/adminAuth';

// R-001 fixtures. Deterministic TEST VECTOR ONLY — not a real secret.
// Identical to the vector in app/src/test/java/com/example/domain/security/AdminAuthTest.kt,
// so any Android/Web divergence breaks this suite.
const TEST_EMAIL = 'owner@example.com';
const TEST_PASSWORD = 'KstrvFixVec-2026-R001';
const TEST_SALT_B64 = 'MDEyMzQ1Njc4OWFiY2RlZg=='; // "0123456789abcdef"
const TEST_HASH_B64 = 'JFL+WmKUf5vtIfaaXIoqnFtIMBTThToSSKKkQJ0PrYE='; // PBKDF2-HMAC-SHA256, 150000 iters
const FIXED_CREDENTIAL = `pbkdf2-sha256$150000$${TEST_SALT_B64}$${TEST_HASH_B64}`;

test('parseCredential: accepts a well-formed credential', () => {
  const parsed = parseCredential(FIXED_CREDENTIAL);
  assert.ok(parsed);
  assert.equal(parsed.iterations, 150_000);
  assert.equal(parsed.salt.length, 16);
  assert.equal(parsed.expectedHash.length, 32);
});

test('parseCredential: fails closed on placeholder / blank / malformed input', () => {
  assert.equal(parseCredential('UNCONFIGURED_CHANGE_ME'), null);
  assert.equal(parseCredential(null), null);
  assert.equal(parseCredential(''), null);
  assert.equal(parseCredential('   '), null);
  assert.equal(parseCredential('sha256$150000$AAAA$BBBB'), null);
  assert.equal(parseCredential(`pbkdf2-sha256$1000$${TEST_SALT_B64}$${TEST_HASH_B64}`), null);
  assert.equal(parseCredential(`pbkdf2-sha256$abc$${TEST_SALT_B64}$${TEST_HASH_B64}`), null);
  assert.equal(parseCredential(FIXED_CREDENTIAL.slice(0, FIXED_CREDENTIAL.lastIndexOf('$'))), null);
});

test('buildAdminCredential: reproduces the fixed vector deterministically', async () => {
  const credential = await buildAdminCredential(TEST_PASSWORD, TEST_SALT_B64, 150_000);
  assert.equal(credential, FIXED_CREDENTIAL);
});

test('verifyAdminCredential: accepts correct credentials', async () => {
  assert.equal(
    await verifyAdminCredential(TEST_EMAIL, TEST_PASSWORD, TEST_EMAIL, FIXED_CREDENTIAL),
    true,
  );
});

test('verifyAdminCredential: compares email case-insensitively', async () => {
  assert.equal(
    await verifyAdminCredential('Owner@Example.COM', TEST_PASSWORD, TEST_EMAIL, FIXED_CREDENTIAL),
    true,
  );
});

test('verifyAdminCredential: rejects wrong email / wrong password / tampered hash', async () => {
  assert.equal(
    await verifyAdminCredential(TEST_EMAIL, 'wrong-password', TEST_EMAIL, FIXED_CREDENTIAL),
    false,
  );
  assert.equal(
    await verifyAdminCredential(TEST_EMAIL, TEST_PASSWORD, 'other@example.com', FIXED_CREDENTIAL),
    false,
  );
  const segments = FIXED_CREDENTIAL.split('$');
  const hash = segments[3];
  const flippedFirst = hash.charAt(0) === 'J' ? 'I' : 'J';
  const tampered = [segments[0], segments[1], segments[2], flippedFirst + hash.slice(1)].join('$');
  assert.equal(
    await verifyAdminCredential(TEST_EMAIL, TEST_PASSWORD, TEST_EMAIL, tampered),
    false,
  );
});

test('verifyAdminCredential: fails closed on missing / unconfigured values', async () => {
  assert.equal(
    await verifyAdminCredential(TEST_EMAIL, TEST_PASSWORD, undefined, FIXED_CREDENTIAL),
    false,
  );
  assert.equal(await verifyAdminCredential(TEST_EMAIL, TEST_PASSWORD, TEST_EMAIL, null), false);
  assert.equal(await verifyAdminCredential(TEST_EMAIL, TEST_PASSWORD, '', FIXED_CREDENTIAL), false);
  assert.equal(await verifyAdminCredential(TEST_EMAIL, TEST_PASSWORD, TEST_EMAIL, ''), false);
  assert.equal(
    await verifyAdminCredential(TEST_EMAIL, TEST_PASSWORD, TEST_EMAIL, 'UNCONFIGURED_CHANGE_ME'),
    false,
  );
  assert.equal(await verifyAdminCredential('', TEST_PASSWORD, TEST_EMAIL, FIXED_CREDENTIAL), false);
  assert.equal(await verifyAdminCredential(TEST_EMAIL, '', TEST_EMAIL, FIXED_CREDENTIAL), false);
});