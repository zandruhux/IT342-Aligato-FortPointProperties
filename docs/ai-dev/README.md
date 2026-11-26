# Claude Code hooks — activation

`claude-settings.json` here is the proposed **project** Claude Code settings file. It wires:

- `PreToolUse` (Read/Edit/Write) → `.claude/hooks/guard-secrets.mjs` — denies `.env`, private keys, credential files (templates like `.env.example` are allowed).
- `PostToolUse` (Edit/Write) → `.claude/hooks/format-web.mjs` — runs `web/`'s Prettier on files Claude edits.
- Deny rules for reading/editing `.env` and force-pushing.

It is kept here (not active) because Claude Code doesn't let the agent install its own hook/permission
configuration — a person should review and enable it. To activate:

```powershell
Copy-Item docs/ai-dev/claude-settings.json .claude/settings.json
```

Then start a new Claude Code session. Both hook scripts were pipe-tested on Windows (Node 20).
