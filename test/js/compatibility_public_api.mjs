import assert from 'node:assert/strict';
import fs from 'node:fs';
import { fileURLToPath } from 'node:url';
import { validateLine } from '../../dist-cljs/index.js';

// The public API deliberately retains ClojureScript maps. Use their existing
// key iterator and lookup methods without inventing a JSON representation.
function field(map, name) {
  const keys = map.keys();
  for (let step = keys.next(); !step.done; step = keys.next()) {
    if (String(step.value) === name) return map.get(step.value);
  }
  return undefined;
}

const fixture = fileURLToPath(new URL('../fixtures/receiver-repo-less-1c4c53.edn', import.meta.url));
const before = fs.readFileSync(fixture);
const lines = before.toString('utf8').trimEnd().split('\n');
const path = '/actual/containing/repository';
let cases = 0;

for (const [index, line] of lines.entries()) {
  const baseline = validateLine(line, 256 + index);
  const contextual = validateLine(line, 256 + index, path);
  assert.equal(field(baseline, ':ok'), false);
  assert.equal(field(contextual, ':ok'), true);
  assert.deepEqual(Array.from(field(contextual, ':errors')), []);
  assert.deepEqual(Array.from(field(contextual, ':source/context-free-errors')),
    Array.from(field(baseline, ':errors')));
  assert.equal(field(contextual, ':line'), line);
  assert.equal(field(contextual, ':line-number'), 256 + index);
  assert.equal(field(field(contextual, ':event'), ':repo'), undefined);
  assert.equal(String(field(field(contextual, ':source/schema'), ':status')), ':unversioned');
  const attribution = field(contextual, ':source/repository');
  assert.equal(field(attribution, ':path'), path);
  assert.equal(String(field(attribution, ':basis')), ':containing-repository');
  assert.equal(String(field(attribution, ':tier')), ':derived');
  cases++;
}

for (const context of [undefined, null, '', ' \n\t', false, 42, [], { repositoryPath: path }]) {
  const result = validateLine(lines[0], 1, context);
  assert.equal(field(result, ':ok'), false);
  assert.deepEqual(Array.from(field(result, ':errors')), ['missing required key: repo']);
  assert.equal(field(result, ':source/repository'), undefined);
  cases++;
}

const payload = ':ts "2026-10-09T00:00:00Z" :kind :observation :origin "probe" :owner "probe" :dod "probe" :pi "probe" :host "local" :manifest [] :refs []';
for (const [repo, valid] of [['nil', false], ['false', true], ['"/explicit"', true]]) {
  const line = `{${payload} :repo ${repo}}`;
  const baseline = validateLine(line, 1);
  const result = validateLine(line, 1, path);
  assert.equal(field(result, ':ok'), valid);
  assert.deepEqual(Array.from(field(result, ':errors')), Array.from(field(baseline, ':errors')));
  assert.equal(field(result, ':source/repository'), undefined);
  assert.equal(field(result, ':line'), line);
  cases++;
}

const envelope = `{:event/id #uuid "00000000-0000-0000-0000-000000000001" :event/type :receipt-river/receipt-recorded :event/recorded-at "2026-10-09T00:00:00Z" :event/schema {:id :eta-mu.receipt-river/receipt-recorded :version 1} :event/producer {:package/name "@eta-mu/receipt-river" :package/version "0.1.0"} :event/subject {:repository/path "/repo"} :event/payload {${payload}}}`;
const declared = validateLine(envelope, 1, path);
assert.equal(field(declared, ':ok'), false);
assert.deepEqual(Array.from(field(declared, ':errors')), ['payload missing required key: repo']);
assert.equal(field(declared, ':source/repository'), undefined);
assert.equal(String(field(field(declared, ':source/schema'), ':status')), ':declared');
cases++;

assert.deepEqual(fs.readFileSync(fixture), before);
console.log(`Built public validateLine: ${cases} cases passed; original receiver fixture unchanged.`);
