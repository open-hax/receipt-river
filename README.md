# @eta-mu/receipt-river

Receipt River owns receipt event construction, schema metadata, validation,
historical receipt compatibility, local repository discovery, and
receipt-specific archaeology projections.

Schema facts live directly in `eta-mu.receipt-river.law.receipt`; package APIs
and the eta-mu composition manifest consume that portable authority without a
prebuild code-generation step.

The canonical application surface is:

```bash
eta-mu receipt ...
```

Historical records without an event envelope remain unversioned. The package
validator reads them for compatibility; it does not assign a schema version
retroactively.

## Containing-repository compatibility

The exported `validateLine(line, lineNumber)` retains its original strict
context-free behavior. `validateLine(line, lineNumber, containingRepositoryPath)`
accepts a nonblank string path supplied by the containing file's reader. The
existing `eta-mu receipt validate` command supplies its checkout root only when
Git resolves that root successfully. On Git failure it still reads the fallback
working directory's ledger, with no repository attribution context: a missing
`:repo` remains a validation error.
The pure `record-errors` law also accepts the closed map
`{:repository/path "<containing path>"}` as its optional second argument.

This context permits only an unversioned map whose `:repo` key is absent.
Declared version1 envelopes remain strict; present nil is still invalid and
existing non-nil values, including false, keep their historical behavior.
Nil, blank or non-string public context input retains context-free validation.
Malformed or extra-key pure context maps do not activate compatibility.

The original `:line`, parsed `:event`, ordinal and source schema remain intact.
Valid context adds `:source/context-free-errors`, preserving every original
diagnostic. When the missing-repo exception applies, `:errors` removes only
that error in its original order and `:source/repository` contains
`{:path "<supplied path>" :basis :containing-repository :tier :derived}`.
Other errors still make the record invalid. Attribution is derived reader
context, not authenticated repository identity, receipt approval or a field
inserted into history. Public results retain their ClojureScript representation.

Run `pnpm test`, `pnpm build`, `pnpm test:public-api` and `pnpm lint:kondo`.
The public API check imports the actual release artifact and retains the frozen
three receiver receipt lines unchanged. The existing required hosted build
gate runs this public-boundary check after the release build.

Repository archaeology preserves clone and worktree identity separately. It
reports added worktrees distinctly from added clones, and emits an explicit
ambiguous location-change observation when repository evidence cannot establish
which new path corresponds to which removed path. Git probes have bounded
lifetimes so a stalled child process cannot block discovery indefinitely.

## Fork sync

After [open-hax/receipt-river#3](https://github.com/open-hax/receipt-river/pull/3) merged as `747d5030`, this fork's `sync/747d503` branch carries that org merge commit back onto fork `main` (E1.11 promote→sync convention). No package tree change beyond the promotion merge itself.

## Explicit completion of missing historical metadata (candidate API)

`validateLedger(lines)` keeps every raw UTF-8 source line and its validation
result. Its `raw-ok` flag never becomes true because of a later correction.
The ledger decoder requires exactly one complete EDN form per source line,
allowing trailing EDN whitespace only. Additional forms, discarded data and
trailing comments refuse admission even when a correction hashes the full
line. The public `validateLine` compatibility behavior remains unchanged;
use `validateLedger` for ordered ledger admission.
A valid versioned adjudication receipt may declare
`:decisions {:receipt-river/metadata-completion {:version 1 :line-number N
:sha256 "<64 hex source line hash>" :fields {:repo "..." :pi "..."}}}`.
Only absent `repo` and `pi` fields may be supplied, and exactly all missing
fields must be supplied as nonblank strings. The target must precede the
adjudication, be unversioned, and have no other validation errors. The SHA256
covers the exact source line without its line terminator. Multiple completions
for one line, invalid adjudications, changed semantic fields and wrong hashes
refuse admission. The derived completed record and correction location remain
explicit beside the unchanged invalid row.

`ok` describes admission with those explicit completions; it does not assert
raw historical validity, producer authentication, approval or deployment trust.
This candidate does not compose independent reflection ledgers or resolve Git
conflicts. Consumers must retain their stream and commit provenance, and adopt
the owner policy through review before using it as a release gate.
