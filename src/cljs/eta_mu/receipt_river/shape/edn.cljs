(ns eta-mu.receipt-river.shape.edn
  "EDN morphisms for line-oriented Receipt River storage."
  (:require [cljs.reader :as reader]
            [cljs.tools.reader.edn :as stream]
            [cljs.tools.reader.reader-types :as reader-types]))

(defn format-line [record]
  (pr-str record))

(defn parse-line [line]
  (reader/read-string line))

(defn- reader-discard? [line]
  ;; Inspect lexical tokens before the standard reader can erase a discard.
  ;; Strings, character literals, comments and whole symbol/keyword tokens
  ;; are opaque, so literal #_ data does not become reader syntax here.
  ;; Numeric literals start with a digit (possibly signed) and stop at #,
  ;; unlike symbols. Preserve that boundary before the reader erases data.
  ;; tools.reader remains responsible for validating the EDN grammar.
  (some #{"#_"}
        (re-seq #"\"(?:\\[\s\S]|[^\"\\])*\"|\\(?:[^\s,\[\]{}();\"\\]+|[\s\S])|;[^\r\n]*|#_|[+-]?[0-9][^\s,\[\]{}();\"\\^#]*|[^\s,\[\]{}();\"\\^]+|[\s\S]"
                line)))

(defn parse-ledger-line
  "Read exactly one form and permit only trailing EDN whitespace. The legacy
   parse-line/validateLine surface retains its original first-form behavior."
  [line]
  (when (reader-discard? line)
    (throw (ex-info "reader discards are not permitted in ledger lines" {})))
  (let [input (reader-types/string-push-back-reader line)
        eof (list ::eof)
        record (stream/read {:eof eof} input)
        trailing (loop [characters []]
                   (if-let [character (reader-types/read-char input)]
                     (recur (conj characters character))
                     (apply str characters)))]
    (when (or (identical? eof record) (not (re-matches #"[\s,]*" trailing)))
      (throw (ex-info "expected exactly one complete EDN form per ledger line" {})))
    record))
