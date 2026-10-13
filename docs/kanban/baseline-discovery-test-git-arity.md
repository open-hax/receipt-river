---
uuid: 7d593487-8163-41e4-97f2-63a9a4bb87fb
title: "Restore the compiled discovery test's Git boundary fixture"
status: "done"
priority: P1
points: 1
labels: receipt-river, tests, later-batch
write-id: "1791531913992-0.ukbtx94bmckcztbchn"
---

# Restore the compiled discovery test's Git boundary fixture

## Context

First hosted run37734293522 on exact35237a3aec20b2c07bf655f9fa1093a292d88a22
compiled the existing test target, then discovery-test crashed with an arity2
property TypeError at the existing Git boundary call. Actual deterministic
artifact11531365779 retains test exit1. Neither producer SUCCESS nor build and
lint passes makes the test suite pass.

This newly discovered work is recorded for a later batch under the accepted
finite-inventory discipline. It is not admitted to the current milestone without
the human's scope decision. The existing containing-repository plan
4caf6f31-9e54-49e0-b6fc-34d2d8414ff0 remains blocked by the mandatory test result.

## Outcome

Run the complete existing compiled test suite without this fixture crash and
retain the bounded-concurrency assertions at their original semantic strength.
The one-point estimate is provisional until independent planning review.

## Scope

Identify the actual failing existing `with-redefs` seam and reproduce the
compiled Node22.20.0 call. The production `git/exec-at` is a multi-arity function;
the current delayed test replacement is a single-arity function. Compiled calls
to the original var may use its arity property. Verify this proposed cause
before repairing the replacement's shape. Keep production discovery, Git timeout,
identity and repository attribution semantics unchanged.

## Non-goals

Receipt compatibility implementation, historical receipt rewrites, unrelated
test suppression, narrowed namespace selection, compiler or package policy
migration, optional-test classification, runtime changes and character features.

## Acceptance criteria

1. The retained old-head hosted output remains inspectable as a real failure;
   its incomplete suite is never relabeled as successful tests.
2. A targeted reproduction establishes the causal fixture/call-shape mismatch,
   or records a different verified cause before any repair.
3. The existing complete `pnpm test` passes with its bounded-concurrency test
   and all original assertions still executed; no skip or selection workaround.
   The replacement retains both arity-2 and arity-3 forms, including the compiled
   `cljs$core$IFn$_invoke$arity$2` entry used by the actual discovery caller.
   Inspect the compiled entries and preserve the in-flight counter and 5 ms delay.
4. Existing build, lint and exact-clean-head hosted gates pass on the actual
   candidate. Publish complete review input and retain native review and gate
   evidence before qualification.

## Verification

Scope acceptance, qualified planning review and native Rheos admission precede
repair. Preserve the old compiled failure, test and Git-boundary source hashes,
then demonstrate failure before the minimal fixture change and success after it.
Run owning package commands, not a substitute local validator or smaller suite.

## Risks

The observed error identifies a call seam, not yet a proven root cause. Avoid
changing production behavior to accommodate an incorrectly shaped test double.
Unlocked dependency resolution is a separate observed limit; one successful
run would not prove all future dependency versions work.


---
Causal diagnostic follow-up 2026-10-08: actual current hosted artifact11531594218 (ZIP SHA2566232419d824870cd9eb053ba77035cd32e41ff21e3b278e664fb7f1b284dc7c1) binds head6370ff968841115e681e2b21d80277fd0f0a0289/base154440f3c997aa9208194bba59b5edbef3654f78, exact/clean, all6gates attempted, test exit1. Its Node22.20.0 stack calls exec_at.cljs$core$IFn$_invoke$arity$2 and reports that property is not a function. The source call, two/three-arity production function and single-arity delayed-git replacement are byte-identical between this head, base and working tree; Git history retains the fixture since initial extraction7c7c62343fea0241ac545e37f14e57ab344bfa30. No prior passing baseline was established. A bounded separately compiled representation probe using existing cached ClojureScript1.12.145/static-fns true/optimizations none on localNode24.14.1 reproduced the same missing-arity-property TypeError with the single-arity replacement; original control and restoration succeed, zero production Git processes. This proves the proposed call-shape mechanism, not the complete owning test, exact hosted environment or a repaired suite. Preserve production timeout/concurrency semantics and original full-suite assertions when a qualified fixture repair is admitted. Proof .ημ/review-evidence/receipt-discovery-arity-causal-diagnostic-20261008.json SHA2568f638d09281c643609c3e1eae3018c3daf0244b82822f0a54e1f22e6ef3020c2. The first private launch used a wrong generated-bootstrap cwd and failed MODULE_NOT_FOUND; retained unchanged output ran correctly from /, not production verification. Free space15,985,807,360bytes is below20GiB; reused existing compiler/classpath only, no full checkout or dependency install. Scope choice remains pending; this card staysIncoming, no owning test/source patch, new estimate, Ready, compatibility implementation, skipped test, provider retry or character runtime change.

Scope authorization 2026-10-09 UTC: the human explicitly answered Include the minimal fixture repair to native question call_968cb17bde984e0c879e6619f31c3d18 in the Cephalon character-design chat. This adds only this one-point baseline test-double prerequisite to the accepted B1/B2/B3 milestone; the earlier pending-scope observations remain historical bytes. The retained causal diagnostic 8f638d09281c643609c3e1eae3018c3daf0244b82822f0a54e1f22e6ef3020c2 proves the compiled single-arity replacement mismatch. Proposed repair is a named two/three-arity delayed test function retaining the same promise, 5ms delay, nine repositories, and all three existing bounded-concurrency assertions. No production Git/discovery/timeout/attribution behavior changes. Complete planning review and native Ready precede repair; then execute unchanged full-suite RED, minimal fixture GREEN, all owning build/lint/test and exact hosted gates, current-input code review and guarded MERGE. This does not complete character encounter, mood or automatic recall and does not resume the paused heartbeat.

Current exact planning approval MiMo5466026271 and CodeRabbit6075302385, with full5466224006 and actual map supplement6075235619, covers all19 inputs. Native Ready/InProgress occurred before minimal repair. Actual pnpm test25tests88assertions0failures0errors, compiled arity2/3 entries both present, 5ms and all original concurrency assertions retained; production sources byte-identical. pnpm build0/0compilerwarnings; lint0errors0warnings/externPASS. Retained old full-suiteRED remains failure. Proof .ημ/review-evidence/receipt-fixture-native-admission-and-green-20261009.json. Fresh exact hosted code gates/review/guardedMERGE still owed; this is not source approval or character B1B2B3 completion.

Qualified source delivered: personal Receipt River PR1 merged5eeb77e035c17160416d0a092415b39b3de55133 at2026-10-09T07:39:40Z, parents154440f3/67f43c2, treeexact reviewed67. Actual MiMoAPPROVED5467136899 assessed all38inputs/41pages; canonical1availablecohort/3PASS/0unsettled PASS and guardedMERGE. Hosted37897227310 actual32tests231assertions0failures0errors/zero lint/compiler warnings/15builtAPIcasesPASS. Original minimal fixture52341b2 preserves delay,counters and all concurrency assertions; old failing evidence remains unchanged. This one-point fixture prerequisite is complete; it does not complete compatibility receiver acceptance or character B1B2B3.

---
