/**
 * R-001 — Local owner-admin authority gate (client-side).
 *
 * Verifies admin credentials against a PBKDF2-HMAC-SHA256 credential string
 * instead of storing or comparing a plaintext password:
 *
 *   pbkdf2-sha256$<iterations>$<Base64(salt 16B)>$<Base64(hash 32B)>
 *
 * SECURITY WARNING (static export): with `output: 'export'` the credential is
 * embedded in the client bundle. It is NOT a secret — it replaces plaintext
 * password storage only. Protection level: CLIENT-SIDE gate for the local
 * dashboard; this is NOT server-side authentication.
 *
 * Fail-closed: any missing/malformed/unconfigured input verifies to `false`.
 * Mirrors Android's `com.example.domain.security.AdminAuth` so both platforms
 * accept the exact same credential values.
 */

const FORMAT_ID = 'pbkdf2-sha256';
const MIN_ITERATIONS = 50_000;
const MAX_ITERATIONS = 10_000_000;
const KEY_LENGTH_BITS = 256;
const KEY_LENGTH_BYTES = KEY_LENGTH_BITS / 8;
const MIN_SALT_BYTES = 8;

export interface ParsedCredential {
  iterations: number;
  salt: Uint8Array;
  expectedHash: Uint8Array;
}

function base64ToBytes(value: string): Uint8Array | null {
  try {
    const binary = atob(value.trim());
    const out = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) out[i] = binary.charCodeAt(i);
    return out;
  } catch {
    return null;
  }
}

function bytesToBase64(bytes: Uint8Array): string {
  let binary = '';
  for (let i = 0; i < bytes.length; i++) binary += String.fromCharCode(bytes[i]);
  return btoa(binary);
}

/** Parses a credential string. Returns null on ANY malformation (fail-closed). */
export function parseCredential(credential: string | null | undefined): ParsedCredential | null {
  if (!credential || !credential.trim()) return null;
  const parts = credential.trim().split('$');
  if (parts.length !== 4 || parts[0] !== FORMAT_ID) return null;
  const iterations = Number.parseInt(parts[1], 10);
  if (!Number.isSafeInteger(iterations) || iterations < MIN_ITERATIONS || iterations > MAX_ITERATIONS) {
    return null;
  }
  const salt = base64ToBytes(parts[2]);
  if (!salt || salt.length < MIN_SALT_BYTES) return null;
  const expectedHash = base64ToBytes(parts[3]);
  if (!expectedHash || expectedHash.length !== KEY_LENGTH_BYTES) return null;
  return { iterations, salt, expectedHash };
}

async function derivePasswordBits(
  password: string,
  salt: Uint8Array,
  iterations: number,
): Promise<Uint8Array | null> {
  try {
    const subtle = globalThis.crypto?.subtle;
    if (!subtle) return null; // fail-closed when no WebCrypto available
    const keyMaterial = await subtle.importKey(
      'raw',
      new TextEncoder().encode(password),
      'PBKDF2',
      false,
      ['deriveBits'],
    );
    const bits = await subtle.deriveBits(
      { name: 'PBKDF2', hash: 'SHA-256', salt: salt as BufferSource, iterations },
      keyMaterial,
      KEY_LENGTH_BITS,
    );
    return new Uint8Array(bits);
  } catch {
    return null; // fail-closed
  }
}

function constantTimeEqual(a: Uint8Array, b: Uint8Array): boolean {
  if (a.length !== b.length) return false;
  let diff = 0;
  for (let i = 0; i < a.length; i++) diff |= a[i]! ^ b[i]!;
  return diff === 0;
}

/**
 * Constant-time credential verification. Returns false on any missing,
 * malformed or unconfigured input (fail-closed).
 */
export async function verifyAdminCredential(
  email: string,
  password: string,
  expectedEmail: string | null | undefined,
  credential: string | null | undefined,
): Promise<boolean> {
  if (!email?.trim() || !password) return false;
  if (!expectedEmail?.trim() || !credential?.trim()) return false;
  const emailMatches = constantTimeEqual(
    new TextEncoder().encode(email.trim().toLowerCase()),
    new TextEncoder().encode(expectedEmail.trim().toLowerCase()),
  );
  if (!emailMatches) return false;
  const parsed = parseCredential(credential);
  if (!parsed) return false;
  const derived = await derivePasswordBits(password, parsed.salt, parsed.iterations);
  if (!derived) return false;
  return constantTimeEqual(derived, parsed.expectedHash);
}

/**
 * Builds a credential string from a password (used by tests / local tooling).
 * Never call this with the real owner password inside CI or committed code.
 */
export async function buildAdminCredential(
  password: string,
  saltBase64?: string,
  iterations = 150_000,
): Promise<string> {
  const salt = saltBase64
    ? base64ToBytes(saltBase64)
    : crypto.getRandomValues(new Uint8Array(16));
  if (!salt || salt.length < MIN_SALT_BYTES) throw new Error('invalid salt');
  const derived = await derivePasswordBits(password, salt, iterations);
  if (!derived || derived.length !== KEY_LENGTH_BYTES) throw new Error('key derivation failed');
  return `${FORMAT_ID}$${iterations}$${bytesToBase64(salt)}$${bytesToBase64(derived)}`;
}