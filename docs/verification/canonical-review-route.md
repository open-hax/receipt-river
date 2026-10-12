# Canonical review route preparation

SPDX-License-Identifier: GPL-3.0-or-later

This caller supplies the missing MiMo execution surface for the existing
containing-repository compatibility plan. It uses the shared eta-mu receiver at
`45ec644c2d15ed511e9bc1e797d1b4073b63dbfc`; it does not copy review mechanics or
review policy into Receipt River. The canonical `pr-flow` skill still decides
planning and code convergence from current native evidence.

## Binding and tools

The caller supplies the immutable native PR head, Node22.20.0, OpenCode1.18.31,
Muse0b9a91492c8355e6933dc2164d35668cb76d9e60 and the current canonical skill
revision4b4f48dae1b804b2581974e604cca618ecedadf1. The pinned receiver supplies
Java21, ClojureCLI1.12.5.1654, Babashka1.13.219, clj-kondo2025.10.23 and
pnpm10.14.0. The producer records installation plus the existing `lint:kondo`,
`test` and `build` package commands and diff hygiene. Failed installation gives
the dependent gates exit125; no skipped gate is a pass.

The package commits `pnpm-lock.yaml`. Installation uses `--frozen-lockfile`,
the same install as `receipt-river-ci.yml`, so the review gates resolve the
dependency versions that release CI exercises, and a lockfile that no longer
matches `package.json` fails the install gate instead of being re-resolved.
A frozen install writes no lockfile, so the exact-clean-head checkout stays
clean. The recorded execution is evidence for that run and that lockfile.

## Publisher and generated inputs

Use the already installed eta-mu-ai App3152464, personal installation118318185,
with its existing signing key forwarded through the two named encrypted Actions
secrets. No new App, installation, identity alias, paid review or publication
destination is introduced. The shared trusted source owns token exchange and
native publication; candidate package commands have no signing-key environment.
Secret metadata and verified App/installation identity can be recorded; secret
contents never enter provenance or logs.

The generated reviewer directories `/.opencode/review-evidence/` and
`/.review-context/` are ignored. This avoids the known Git-quoted Unicode
generated-path problem in the currently qualified receiver. The first actual
hosted run also created `/.clj-kondo/.cache/`; that generated cache is now
ignored at its exact path. The enclosing lint configuration stays visible.
These rules leave other untracked files and tracked
source/provenance modifications visible. It is caller preparation, not repair
or settlement of the open shared-receiver finding.

## Admission boundary

The incoming compatibility card remains prospective. Existing baseline tests
may verify the new review route; no new behavior test, product law, compatibility
implementation, Rheos Ready or character runtime is supplied here. Reusing the
existing reviewer credentials and package commands is review preparation, not
the card's excluded new credential installation or CI semantics.

After push, inspect the actual hosted deterministic artifact, full-input and
submission proofs and authenticated native verdict before claiming this route
operational. Producer or workflow SUCCESS alone does not make a failing package
gate pass or grant approval. The planning review must cover this caller, ignore
rules, this document, the complete card and every receipt/reflection suffix.
Original receipt/reflection prefixes remain immutable. Only current-head
qualified planning review permits the later native Rheos admission and RED.

## First hosted observation

Run37734293522 on exact35237a3aec20b2c07bf655f9fa1093a292d88a22 executed
all six configured gates: diff stat, diff hygiene, install, lint and build
returned0, while the existing test command returned1. Its completed producer
job was SUCCESS, but actual artifact11531365779 records result FAILURE and
clean checkout FALSE because the lint cache was untracked. The package test
compiled82files with0compilerwarnings, then crashed in discovery-test with
`TypeError: eta_mu.receipt_river.extern.git.exec_at.cljs$core$IFn$_invoke$arity$2
is not a function`. No complete test count or compatibility pass is claimed.
The unlocked installation resolved shadow-cljs3.5.5 for this run.

The generated-cache rule repairs only the observed checkout hygiene issue.
The actual test failure remains a delivery blocker and is recorded for later
inventory review; no discovery source or fixture is silently changed, skipped
or converted to passing. Fresh current-head hosted execution must demonstrate
the effect of the cache rule, including any remaining test failure.
