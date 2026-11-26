#!/usr/bin/env node
// PreToolUse hook (Read|Edit|Write): stop Claude from reading or changing secret files.
// Templates such as .env.example / .env.sample stay allowed.
import { basename } from 'node:path';

const BLOCKED = [
  /^\.env(\..+)?$/i, // .env, .env.local, .env.production ...
  /\.(pem|key|p12|pfx|jks|keystore)$/i,
  /^credentials(\..+)?\.json$/i,
  /^service-account.*\.json$/i,
  /^id_(rsa|ed25519|ecdsa)$/i,
];
const ALLOWED = [/^\.env\.(example|sample|template)$/i];

let raw = '';
process.stdin.on('data', (c) => (raw += c));
process.stdin.on('end', () => {
  let filePath = '';
  try {
    const input = JSON.parse(raw || '{}');
    filePath = input.tool_input?.file_path || input.tool_input?.path || '';
  } catch {
    process.exit(0); // never block on malformed input
  }
  if (!filePath) process.exit(0);

  const name = basename(filePath);
  if (ALLOWED.some((r) => r.test(name)) || !BLOCKED.some((r) => r.test(name))) process.exit(0);

  process.stdout.write(
    JSON.stringify({
      hookSpecificOutput: {
        hookEventName: 'PreToolUse',
        permissionDecision: 'deny',
        permissionDecisionReason: `"${name}" looks like a secrets file and is protected by .claude/hooks/guard-secrets.mjs. Ask the user to make this change themselves, or use the matching .env.example template.`,
      },
    }),
  );
});
