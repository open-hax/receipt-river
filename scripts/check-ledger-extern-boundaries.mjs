import fs from "node:fs";
import path from "node:path";
import process from "node:process";
import { fileURLToPath } from "node:url";

const repositoryRoot = path.dirname(path.dirname(fileURLToPath(import.meta.url)));

// This repository owns one package. The donor monorepo passed sibling names
// here; an extracted repository can only lint its own sources.
const packageNames = process.argv[2] ? [process.argv[2]] : ["receipt-river"];

const ownPackageName = "receipt-river";

const forbiddenForms = [
  ["Raw JavaScript escape hatch", /\bjs\*/],
  ["JavaScript global", /\bjs\//],
  ["JavaScript constructor helper", /\bjs-/],
  ["JavaScript literal", /#js\b/],
  ["JavaScript data conversion", /\b(?:clj->js|js->clj|array-seq)\b/],
  ["JavaScript property or method interop", /\(\.-?[A-Za-z_$]/],
  ["foreign Node module import", /\[\s*"node:/],
];

function cljsFiles(directory) {
  return fs.readdirSync(directory, { withFileTypes: true }).flatMap((entry) => {
    const entryPath = path.join(directory, entry.name);
    if (entry.isDirectory()) {
      return cljsFiles(entryPath);
    }
    return entry.isFile() && entry.name.endsWith(".cljs") ? [entryPath] : [];
  });
}

const violations = [];

for (const packageName of packageNames) {
  // Standalone layout. The donor kept these sources under
  // packages/<name>/src/cljs; this repository is the package.
  const sourceRoot = path.join(repositoryRoot, "src", "cljs");
  if (packageName !== ownPackageName) {
    violations.push(
      `${packageName}: not owned by this repository (${ownPackageName})`,
    );
    continue;
  }
  if (!fs.existsSync(sourceRoot)) {
    violations.push(`${packageName}: ${path.relative(repositoryRoot, sourceRoot)} does not exist`);
    continue;
  }

  for (const file of cljsFiles(sourceRoot)) {
    if (file.split(path.sep).includes("extern")) {
      continue;
    }

    fs.readFileSync(file, "utf8")
      .split(/\r?\n/)
      .forEach((line, index) => {
        for (const [label, pattern] of forbiddenForms) {
          if (pattern.test(line)) {
            violations.push(
              `${path.relative(repositoryRoot, file)}:${index + 1}: ${label}: ${line.trim()}`,
            );
          }
        }
      });
  }
}

if (violations.length > 0) {
  console.error("Raw host access is only allowed in extern namespaces:");
  console.error(violations.join("\n"));
  process.exit(1);
}

console.log(`Extern boundary check passed: ${packageNames.join(", ")}`);
