#!/usr/bin/env node
// PostToolUse hook (Edit|Write): run the web project's Prettier on files Claude
// just edited under web/. Silent no-op for anything else (Java, docs, ...).
import { spawnSync } from 'node:child_process';
import { existsSync } from 'node:fs';
import { join, relative, resolve, sep } from 'node:path';

const EXTENSIONS = /\.(js|jsx|ts|tsx|json|css|md|html)$/i;

let raw = '';
process.stdin.on('data', (c) => (raw += c));
process.stdin.on('end', () => {
  try {
    const input = JSON.parse(raw || '{}');
    const filePath = input.tool_input?.file_path || input.tool_response?.filePath;
    if (!filePath || !EXTENSIONS.test(filePath)) return;

    const root = process.env.CLAUDE_PROJECT_DIR || process.cwd();
    const webDir = resolve(root, 'web');
    const abs = resolve(filePath);
    const rel = relative(webDir, abs);
    if (rel.startsWith('..') || rel.includes(`node_modules${sep}`)) return;

    const prettier = join(webDir, 'node_modules', 'prettier', 'bin', 'prettier.cjs');
    if (!existsSync(prettier)) return; // deps not installed yet

    spawnSync(process.execPath, [prettier, '--write', '--ignore-unknown', '--log-level', 'warn', rel], {
      cwd: webDir,
      stdio: 'ignore',
      timeout: 20000,
    });
  } catch {
    // formatting must never break the edit
  }
});
