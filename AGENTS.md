# Receipt River — Agent Guidance

Append-only receipt ledger for agent decisions and execution state.

## Quick commands

```bash
# Build (ESM library)
pnpm build

# Tests
pnpm test

# Lint
pnpm lint:kondo

# Watch
pnpm watch

# Clean
pnpm clean
```

## Architecture

- Receipt event construction/validation
- Historical unversioned compatibility
- Provider-independent local repository discovery
- ESM exports: buildEvent, validateLine, schemaRegistry, currentSchemas

## Dependencies

- Maven: malli (via shadow-cljs)
- npm: shadow-cljs
- Node built-ins: fs, path, os, child_process
- No workspace or sibling dependencies

## License

GPL-3.0-or-later
